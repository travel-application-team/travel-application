#!/usr/bin/env python
# coding: utf-8

# In[1]:

get_ipython().run_line_magic('matplotlib', 'inline')

import matplotlib.font_manager as fm


# In[2]:

from flask import Flask, jsonify, Response
import pandas as pd
import requests
import math
import json
import random
import pickle

seoul_codes=["11110","11140","11170", "11200", "11215", "11230",
           "11260", "11290", "11305", "11320", "11350", "11380",
           "11410", "11440", "11470", "11500", "11530", "11545",
           "11560", "11590", "11620", "11650", "11680", "11710", "11740"] # 11

busan_codes=["26110", "26140", "26170", "26200", "26230", "26260",
             "26290", "26320", "26350", "26380", "26410", "26440",
             "26470", "26500", "26530", "26710"] # 26

daegu_codes=["27110", "27140", "27170", "27200", "27230", "27260",
             "27290", "27710", "27720"] #27

incheon_codes=["28110", "28140", "28177", "28185", "28200", "28237",
               "28245", "28260", "28710", "28720"] #28

gwangju_codes=["29110", "29140", "29155", "29170", "29200"] #29

daejeon_codes=["30110", "30140", "30170", "30200", "30230"] #30

ulsan_codes=["31110", "31140", "31170", "31200", "31710"] #31

sejong_codes=["36110"] #36

gyeonggi_codes=["41111", "41113", "41115", "41117", "41131", "41133",
                "41135", "41150", "41171", "41173", "41192", "41194",
                "41196", "41210", "41220", "41250", "41271", "41273",
                "41281", "41285", "41287", "41290", "41310", "41360",
                "41370", "41390", "41410", "41430", "41450", "41461",
                "41463", "41465", "41480", "41500", "41550", "41570",
                "41590", "41610", "41630", "41650", "41670", "41800",
                "41820", "41830"] #41

chungbuk_codes=["43111", "43112", "43113", "43114", "43130", "43150",
                "43720", "43730", "43740", "43745", "43750", "43760",
                "43770", "43800"] #43

chungnam_codes=["44131", "44133", "44150", "44180", "44200", "44210",
                "44230", "44250", "44270", "44710", "44760", "44770",
                "44790", "44800", "44810", "44825"] # 44

jeonnam_codes=["46110", "46130", "46150", "46170", "46230", "46710",
               "46720", "46730", "46770", "46780", "46790", "46800",
               "46810", "46820", "46830", "46840", "46860", "46870",
               "46880", "46890", "46900", "46910"] # 46

gyeongbuk_codes=["47111", "47113", "47130", "47150", "47170", "47190",
                 "47210", "47230", "47250", "47280", "47290", "47730",
                 "47750", "47760", "47770", "47820", "47830", "47840",
                 "47850", "47900", "47920", "47930", "47940"] # 47

gyeongnam_codes=["48121", "48123", "48125", "48127", "48129", "48170",
                 "48220", "48240", "48250", "48270", "48310", "48330",
                 "48720", "48730", "48740", "48820", "48840", "48850",
                 "48860", "48870", "48880", "48890"] # 48

jeju_codes=["50110", "50130"] # 50

gangwon_codes=["51110", "51130", "51150", "51170", "51190", "51210",
               "51230", "51720", "51730", "51750", "51760", "51770",
               "51780", "51790", "51800", "51810", "51820", "51830"] # 51

jeonbuk_codes=["52111", "52113", "52130", "52140", "52180", "52190",
               "52210", "52710", "52720", "52730", "52740", "52750",
               "52770", "52790", "52800"] # 52


app = Flask(__name__)

API_URL="http://apis.data.go.kr/B551011/TarRlteTarService1/areaBasedList1"
SERVICE_KEY="41fd329d3da1b3a48fa07d3f2a01696145aa2d63d0e9294fe5f28dff30b7716d"

numOfRows=1000

params = {
    "serviceKey": SERVICE_KEY,
    "pageNo": 1,
    "numOfRows": 1,
    "MobileOS": "WEB",
    "MobileApp": "travel-application",
    "baseYm": "202506",
    "_type": "json"
}

areaCd="52"

all_dataframes=[]

for sigungu_code in jeonbuk_codes:
    params.update({
        "areaCd": areaCd,
        "signguCd": sigungu_code,
        "pageNo": 1,
        "numOfRows": 1

    })
    res = requests.get(API_URL, params=params).json()
    total_count = res["response"]["body"]["totalCount"]

    if total_count==0:
        print(f"데이터 없음: 시군구 코드 {sigungu_code}")
        continue

    total_pages=math.ceil(total_count/numOfRows)

    for page in range(1, total_pages+1):
        params.update({
            "pageNo": page,
            "numOfRows": numOfRows
        })

        res=requests.get(API_URL, params=params).json()
        places=res["response"]["body"]["items"]["item"]

        selected_columns=[
            {
                "tAtsCd": place["tAtsCd"],
                "tAtsNm": place["tAtsNm"],
                "areaCd": place["areaCd"],
                "areaNm": place["areaNm"],
                "signguCd": place["signguCd"],
                "signguNm": place["signguNm"],
                "rlteTatsCd": place["rlteTatsCd"],
                "rlteTatsNm": place["rlteTatsNm"],
                "rlteRegnCd": place["rlteRegnCd"],
                "rlteRegnNm": place["rlteRegnNm"],
                "rlteSignguCd": place["rlteSignguCd"],
                "rlteSignguNm": place["rlteSignguNm"],
                "rlteCtgryLclsNm": place["rlteCtgryLclsNm"],
                "rlteCtgryMclsNm": place["rlteCtgryMclsNm"],
                "rlteCtgrySclsNm": place["rlteCtgrySclsNm"],
                "rlteRank": place["rlteRank"]
            }
            for place in places
        ]

        df=pd.DataFrame(selected_columns)
        all_dataframes.append(df)

final_df=pd.concat(all_dataframes, ignore_index=True)

final_df.to_pickle("jeonbuk_data.pkl")

if __name__ == "__main__":
    app.run(debug=True)


files=["seoul_data.pkl", "busan_data.pkl", "daegu_data.pkl", "incheon_data.pkl",
       "gwangju_data.pkl", "daejeon_data.pkl", "ulsan_data.pkl", "sejong_data.pkl",
       "gyeonggi_data.pkl", "chungbuk_data.pkl", "chungnam_data.pkl", "jeonnam_data.pkl",
       "gyeongbuk_data.pkl", "gyeongnam_data.pkl", "jeju_data.pkl", "gangwon_data.pkl", "jeonbuk_data.pkl"]

dfs=[pd.read_pickle(file) for file in files]
df_total=pd.concat(dfs, ignore_index=True)

review_keywords=["직원이 친절해요", "경치가 좋아요",
                 "포토스팟이 많아요", "동선이 잘 짜였어요",
                 "시설이 깨끗해요", "접근성이 좋아요",
                 "아이와 함께 가기 좋아요", "한적하고 여유로워요",
                 "가성비가 좋아요", "전시/인테리어 연출이 멋져요"]

df_total["리뷰키워드"]=df_total.apply(
    lambda row: random.sample(review_keywords, 5), axis=1
)

df_total.to_pickle("places_data.pkl")

with open("review_keywords.pkl", "wb") as f:
    pickle.dump(review_keywords, f)