package travel.travelapplication.place.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import travel.travelapplication.auth.CustomOAuth2User;
import travel.travelapplication.place.dto.TagListRequest;
import travel.travelapplication.place.dto.TagListResponse;
import travel.travelapplication.place.application.TagService;
import travel.travelapplication.user.application.UserService;

import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TagController {

  private final UserService userService;
  private final TagService tagService;

  @GetMapping("/tags")
  public List<String> tags() {
    return tagService.findAll();
  }

  @PostMapping("/tag")
  public ResponseEntity<TagListResponse> addTag(
      @AuthenticationPrincipal CustomOAuth2User oAuth2User,
      @RequestBody TagListRequest tagListRequest) {
    TagListResponse tagListResponse = userService.addTag(oAuth2User, tagListRequest.ids());

    return ResponseEntity.ok(tagListResponse);
  }

  @GetMapping("/myroom/taglist")
  public ResponseEntity<TagListResponse> userTagList(
      @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
    TagListResponse tagListResponse = userService.findAllTag(oAuth2User);

    return ResponseEntity.ok(tagListResponse);
  }
}
