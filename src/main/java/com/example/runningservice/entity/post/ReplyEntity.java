package com.example.runningservice.entity.post;

import com.example.runningservice.entity.BaseEntity;
import com.example.runningservice.entity.MemberEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditOverride;

@Entity(name = "reply")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class ReplyEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @NotNull
    private String content;

    public static ReplyEntity of(PostEntity postEntity, MemberEntity memberEntity, String content) {
        return ReplyEntity.builder()
            .post(postEntity)
            .member(memberEntity)
            .content(content)
            .build();
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
