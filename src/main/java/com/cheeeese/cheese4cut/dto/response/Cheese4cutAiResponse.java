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
}