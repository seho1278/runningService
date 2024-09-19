package com.example.runningservice.entity.post;

import com.example.runningservice.dto.post.PostRequestDto;
import com.example.runningservice.dto.post.UpdatePostRequestDto;
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
import java.util.Collection;
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
    private List<String> images = new ArrayList<>();

    private Boolean isNotice;

    @OneToMany
    @JoinColumn(name = "comment_id")
    private List<CommentEntity> comment = new ArrayList<>();


    public static PostEntity of(Long memberId, Long crewId, PostRequestDto postRequestDto) {
        return PostEntity.builder()
            .title(postRequestDto.getTitle())
            .memberId(memberId)
            .crewId(crewId)
            .postCategory(postRequestDto.getPostCategory())
            .activityId(postRequestDto.getActivityId())
            .content(postRequestDto.getContent())
            .isNotice(postRequestDto.getIsNotice())
            .build();
    }

    public void addPostImages(Collection<String> imageUrls) {
        if (this.images == null) {
            this.images = new ArrayList<>(); // 명시적으로 다시 초기화
        }
        this.images.addAll(imageUrls);
    }

    public void savePostImages(Collection<String> imageUrls) {
        this.images = new ArrayList<>(imageUrls);
    }

    public void updatePost(UpdatePostRequestDto updatePostRequestDto) {
        this.title = updatePostRequestDto.getTitle();
        this.content = updatePostRequestDto.getContent();
        this.activityId = updatePostRequestDto.getActivityId();
        this.isNotice = updatePostRequestDto.getIsNotice();
    }
}
