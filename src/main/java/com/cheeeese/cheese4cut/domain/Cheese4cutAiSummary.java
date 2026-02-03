package com.cheeeese.cheese4cut.domain;

import com.cheeeese.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "cheese4cut_ai_summary")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cheese4cutAiSummary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cheese4cut_id", nullable = false, unique = true)
    private Cheese4cut cheese4cut;

    @Column(name = "ai_title", length = 30) // 10자 내외
    private String aiTitle;

    @Column(name = "ai_content", length = 1000) // 180~220자 내외
    private String aiContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AiSummaryStatus status;

    @Builder
    public Cheese4cutAiSummary(Cheese4cut cheese4cut, String aiTitle, String aiContent, AiSummaryStatus status) {
        this.cheese4cut = cheese4cut;
        this.aiTitle = aiTitle;
        this.aiContent = aiContent;
        this.status = status;
    }

    public void updateStatus(AiSummaryStatus status, String title, String content) {
        this.status = status;
        this.aiTitle = title;
        this.aiContent = content;
    }
}
