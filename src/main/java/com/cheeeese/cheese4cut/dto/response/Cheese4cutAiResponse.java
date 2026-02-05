package com.cheeeese.cheese4cut.dto.response;

public record Cheese4cutAiResponse(
        String status,
        String title,
        String content
) {
    public static Cheese4cutAiResponse processing() {
        return new Cheese4cutAiResponse("PROCESSING", null, null);
    }

    public static Cheese4cutAiResponse completed(String title, String content) {
        return new Cheese4cutAiResponse("COMPLETED", title, content);
    }

    public static Cheese4cutAiResponse failed() {
        return new Cheese4cutAiResponse("FAILED", "요약 실패", "AI 분석 중 오류가 발생했습니다.");
    }
}