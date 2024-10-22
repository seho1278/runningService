package com.example.runningservice.dto.post;

import com.example.runningservice.enums.PostCategory;
import com.example.runningservice.util.validator.RequiredActivityId;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@RequiredActivityId(categoryType = PostCategory.class, category = "postCategory", idType = Long.class, id = "activityId")
public class UpdatePostRequestDto extends PostRequestDto {

    @NotNull
    private Long postId;
    @NotNull
    private Boolean deleteAllImages;
    private List<String> imagesToDelete = new ArrayList<>();

}
