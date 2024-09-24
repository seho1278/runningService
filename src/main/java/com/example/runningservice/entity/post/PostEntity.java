package com.example.runningservice.entity.post;

import com.example.runningservice.dto.post.CreatePostRequestDto;
import com.example.runningservice.entity.BaseEntity;
import com.example.runningservice.enums.PostCategory;
import jakarta.annotation.Nullable;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditOverride;

@Entity(name = "post")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class PostEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long memberId;
    @NotNull
    private Long crewId;
    @Nullable
    private Long activityId;

    private String title;

    @Enumerated(EnumType.STRING)
    private PostCategory postCategory;

    private String content;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "post_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "images")
    private List<String> imageUrls = new ArrayList<>();

    private Boolean isNotice;

    @OneToMany
    @JoinColumn(name = "comment_id")
    private List<CommentEntity> comment = new ArrayList<>();


    public static PostEntity of(Long memberId, Long crewId, CreatePostRequestDto createPostRequestDto) {
        return PostEntity.builder()
            .title(createPostRequestDto.getTitle())
            .memberId(memberId)
            .crewId(crewId)
            .postCategory(createPostRequestDto.getPostCategory())
            .activityId(createPostRequestDto.getActivityId())
            .content(createPostRequestDto.getContent())
            .isNotice(createPostRequestDto.getIsNotice())
            .build();
    }

    public void savePostImages(List<String> imageUrls) {
        if (this.imageUrls == null) {
            this.imageUrls = new ArrayList<>(); // 명시적으로 다시 초기화
        }
        this.imageUrls.addAll(imageUrls);
    }
}
