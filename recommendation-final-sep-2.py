#!/usr/bin/env python
# coding: utf-8

# In[ ]:


#!/usr/bin/env python
# coding: utf-8

import pandas as pd
import glob
import seaborn as sns
import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')

import matplotlib.font_manager as fm
import re

# ## 장소, 리뷰키워드 로드
places_df = pd.read_pickle("places_data.pkl")
with open("review_keywords.pkl", "rb") as f:
    keywords_list = pickle.load(f)

# 모든 장소의 모든 키워드를 0으로 초기화
def one_hot_keywords(row_keywords, all_keywords):
    s = pd.Series(0, index=all_keywords, dtype="int8")
    for k in row_keywords:
        if k in s.index:
            s[k] = 1
    return s

features_df = places_df["리뷰키워드"].apply(lambda ks: one_hot_keywords(ks, keywords_list))
features_df.index = places_df["tAtsCd"]  # placeId
features_df.sort_index(inplace=True)

place_info = pd.DataFrame({
    "name": places_df["tAtsNm"],
    "address": (places_df["areaNm"].fillna("") + " " + places_df["signguNm"].fillna("")).str.strip() + " " + places_df["rlteTatsNm"].fillna(""),
    "city": places_df["areaNm"],
    "district": places_df["signguNm"]
}, index=places_df["tAtsCd"])

features_df.to_pickle("./review_keywords.p")
features_df.to_pickle("./reviews.p")

# ## 좋아요(평점) 분석

review_keywords = pd.read_pickle('./review_keywords.p')
likes = pd.read_csv('./like.csv')


# 데이터프레임 병합
likes = likes.merge(review_keywords, left_on='placeId', right_index=True)
likes = likes.merge(place_info, left_on='placeId', right_index=True)


# '친절도' 열을 3번째 열로 이동
likes.insert(3, 'name', likes.pop('name'))

likes.to_pickle('./likes_update.p')
likes = pd.read_pickle('./likes_update.p')


# In[ ]:


## Test Train Split

from sklearn.model_selection import train_test_split

train, test = train_test_split(likes, random_state=42, test_size=.1)

from sklearn.model_selection import RandomizedSearchCV
from scipy.stats import uniform as sp_rand
from sklearn.linear_model import Lasso

reviews_cols=review_keywords.columns


## 전체 user

review_keywords = pd.read_pickle('./reviews.p')

from sklearn.linear_model import LinearRegression

from sklearn.linear_model import Lasso
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import GridSearchCV

# 전체 데이터에서 최적의 alpha 값 찾기
x_train_all = train[review_keywords.columns]
y_train_all = train['like']

# 특성 정규화
scaler = StandardScaler()
x_train_all_scaled = scaler.fit_transform(x_train_all)

# GridSearchCV를 이용한 하이퍼파라미터 튜닝
alpha_range = {'alpha': [0.01, 0.1, 1, 10, 100]}
lasso = Lasso()
rsearch = GridSearchCV(lasso, alpha_range, cv=5)
rsearch.fit(x_train_all_scaled, y_train_all)
alpha = rsearch.best_estimator_.alpha

# 사용자별로 모델 학습
user_profile_list = []

for userId in train['userid'].unique():
    user = train[train['userid'] == userId]
    x_train = user[review_keywords.columns]  # 특성
    y_train = user['like']  # 라벨

    # 각 사용자 데이터 정규화
    x_train_scaled = scaler.transform(x_train)

    # 최적 alpha로 Lasso 모델 학습
    reg = Lasso(alpha=alpha)
    reg.fit(x_train_scaled, y_train)

    user_profile_list.append([reg.intercept_, *reg.coef_])

user_profiles = pd.DataFrame(user_profile_list, index=train['userid'].unique(), # user를 인덱스로
                            columns=['intercept', *review_keywords.columns])

user_profile_lasso = pd.DataFrame(user_profile_list,
                            index=train['userid'].unique(),
                            columns=['intercept', *review_keywords.columns])




# In[ ]:


from tqdm import tqdm_notebook

# 평점 예측
predict = []

for idx, row in tqdm_notebook(test.iterrows()):
  user = row['userid'] # test row에 user 데이터가 들어옴

  # 해당 user의 profile에서 intercept 값을 가져옴
  intercept = user_profile_lasso.loc[user, 'intercept']

  # 해당 장소의 카테고리에서 비롯되는 예상 점수
  category_score = sum(user_profile_lasso.loc[user, review_keywords.columns] * row[review_keywords.columns])
  expected_score = intercept + category_score
  predict.append(expected_score)

test['predict_lasso'] = predict

# DataFrame에서 마지막 열을 선택하여 Series로 가져옵니다.
last_column = test[test.columns[-1]]

# 마지막 열을 제외한 열들을 선택합니다.
other_columns1 = test[test.columns[5:-1]]
other_columns2 = test[test.columns[0:4]]

# 원하는 순서로 열을 재배열하여 새로운 DataFrame을 생성합니다.
new_df = pd.concat([other_columns2, last_column,other_columns1], axis=1)

final_df = new_df[(new_df['userid']==5) & (new_df['like']==1)].sort_values(by='predict_lasso', ascending=False).head(1)

selected = ['userid', 'placeId', 'name','address', 'predict_lasso']

final_df = final_df[selected]


# ## 평가지표

from sklearn.metrics import mean_squared_error
import numpy as np

rmse = np.sqrt(mean_squared_error(test['like'], test['predict_lasso']))

intercept = reg.intercept_
coef = reg.coef_


# In[ ]:


# ## Spring과 연동

# 설치가 필요할 경우
# !pip install Flask

from flask import Flask, jsonify, Response. request
import pickle
from werkzeug.exceptions import BadRequest

app = Flask(__name__)

def to_json_records(df) -> Response:
    # 자바에서 바로 List<Recommendation>로 역직렬화할 수 있도록 records 배열로 반환
    data_json = df.to_json(orient='records', force_ascii=False)
    return Response(data_json, content_type='application/json; charset=utf-8')

@app.route('/data', methods=['GET'])
def get_data():
    return to_json_records(final_df)

@app.route('/send-places', methods=['POST'])
def send_places():
    try:
        payload = request.get_json(force=True, silent=False)
    except BadRequest:
        return jsonify({"message": "Invalid JSON body"}), 400

    ## 입력값 검증
    # 필드 검증
    missing = [k for k in ("email", "city", "district", "period") if k not in payload]
    if missing:
        return jsonify({"message": f"Missing fields: {', '.join(missing)}"}), 400

    # period 정수 검증
    try:
        int(payload["period"])
    except (ValueError, TypeError):
        return jsonify({"message": "period must be an integer (days)"}), 400

    top_k = max(1, period*5) # 일정(기간)의 5배 장소 추천 - 추후 조절 가능

    ## 지역 필터링 후 상위 k개 반환
    base_df = new_df.copy()

    for col in ("city", "district", "predict_lasso"):
        if col not in base_df.columns:
            return jsonify({"message": f"'{col}' column not found in result table"}), 500

    base_df = base_df[(base_df["city"] == city) & (base_df["district"] == district)]

    if base_df.empty:
        return jsonify([]), 200

    result_df = base_df.sort_values("predict_lasso", ascending=False).head(top_k)

    preferred_cols = ["userid", "placeId", "name", "address", "predict_lasso"]
    output_cols = [c for c in preferred_cols if c in result_df.columns]
    if output_cols:
        result_df = result_df[output_cols]

    return to_json_records(result_df)


if __name__ == '__main__':
    app.run(port=5000)