package com.example.runningservice.dto.post;

import com.example.runningservice.enums.PostCategory;
import com.example.runningservice.util.validator.RequiredActivityId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@RequiredActivityId(categoryType = PostCategory.class, category = "postCategory", idType = Long.class, id = "activityId")
public class PostRequestDto {

    @NotBlank
    @Size(max = 50)
    private String title;

    @NotNull
    private PostCategory postCategory;

    private Long activityId;

    @NotBlank
    @Size(max = 500)
    private String content;

    private List<MultipartFile> imagesToUpload = new ArrayList<>();

    private Boolean isNotice = false;

    public void setActivityIdNullNotWithActivityReview() {
        if (this.postCategory == null || !this.postCategory.equals(PostCategory.ACTIVITY_REVIEW)) {
            this.activityId = null;
        }
    }
}
