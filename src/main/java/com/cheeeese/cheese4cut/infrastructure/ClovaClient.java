package com.cheeeese.cheese4cut.infrastructure;

import com.cheeeese.cheese4cut.dto.response.AiResult;
import com.cheeeese.global.common.code.ErrorCode;
import com.cheeeese.global.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClovaClient {

    @Value("${clova.api.key}") private String apiKey;
    @Value("${clova.api.hcx-url}") private String hcxUrl;
    @Value("${clova.api.dash-url}") private String dashUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 프롬프트 1: 이미지 분석용 (HCX-005) - 파이썬 코드의 request_data 반영
    private static final String SYSTEM_PROMPT_005 =
            "너는 사진을 해석하는 관찰 장치다.\n\n" +
                    "너의 임무는 이 사진을 보고,\n" +
                    "1) 눈에 보이는 사실\n" +
                    "2) 그 사실로부터 가능한 맥락\n" +
                    "을 분리해서 기록하는 것이다.\n\n" +
                    "이 결과는 다른 언어 모델이 여러 장의 사진을 하나의 이야기로 합치는 데 사용된다.\n\n" +
                    "규칙:\n" +
                    "- 사실은 오직 사진에서 직접 확인할 수 있는 것만 쓴다.\n" +
                    "- 맥락은 “그럴 수 있음”의 형태로만 쓴다. 단정하지 않는다.\n" +
                    "- 사람의 이름, 관계, 정확한 장소, 날짜, 이벤트는 절대 만들어내지 않는다.\n" +
                    "- 감정(기쁨, 슬픔, 행복 등)은 얼굴 표정이나 행동으로 명확할 때만 제한적으로 언급한다.\n" +
                    "- 사진 속 글자는 보이는 그대로 기록한다.\n" +
                    "- 불확실하면 “불명확함”이라고 적는다.\n\n" +
                    "출력 형식:\n\n" +
                    "[사실]\n- …\n- …\n- …\n\n" +
                    "[맥락 추론]\n- (가능성) … — 근거: …\n- (가능성) … — 근거: …\n\n" +
                    "[핵심 키워드]\n- …\n- …";

    // 프롬프트 2: 4장 요약용 (HCX-DASH-002) - 파이썬 코드의 request_data 반영
    private static final String SYSTEM_PROMPT_DASH =
            "너는 사용자의 이벤트를 사진으로 정리해 주는 앨범 편집자다.\n\n" +
                    "입력으로 다음이 주어진다:\n" +
                    "- 이벤트 제목\n" +
                    "- 이벤트 날짜\n" +
                    "- 4장의 사진 기록 (각각 [사실], [맥락 추론], [키워드]로 구성)\n\n" +
                    "이 정보들은 사용자가 실제로 경험한 하루나 이벤트를 기록하기 위한 재료이다.\n\n" +
                    "너의 목표는:\n" +
                    "- 이벤트 제목과 날짜를 자연스럽게 문장에 녹여\n" +
                    "- 네 장의 사진이 함께 담고 있는 순간을 하나의 짧은 앨범 기록으로 만들고\n" +
                    "- 사용자가 다시 보았을 때 그날을 떠올릴 수 있는 텍스트를 제공하는 것이다.\n\n" +
                    "규칙:\n" +
                    "- [사실]을 가장 신뢰할 수 있는 정보로 사용한다.\n" +
                    "- [맥락 추론]은 흐름과 분위기를 보완하는 정도로 사용한다.\n" +
                    "- 인물 이름, 관계, 정확한 장소, 세부 사건을 만들어내지 않는다.\n" +
                    "- “이 사진은 ~”처럼 분석·설명하는 문장은 사용하지 않는다.\n" +
                    "- 사용자에게 말하듯 자연스럽게, 하지만 과장 없이 쓴다.\n" +
                    "- 사진에서 나온 구체 요소(사람, 행동, 사물, 배경)를 최소 두 개 이상 포함한다.\n" +
                    "- 사진에는 순서가 없으므로 시간 흐름을 임의로 만들지 않는다.\n\n" +

                    "제목은 특수문자를 제외하고 공백 포함 10자 이내로 작성한다.\n" +
                    "내용은 공백 포함 250자 이내로 작성한다.\n\n" +

                    "⚠️ 매우 중요: 출력은 반드시 아래 JSON 형식으로만 작성한다.\n" +
                    "다른 설명, 줄글, 마크다운(**), 제목 표시, 부가 텍스트를 절대 추가하지 마라.\n\n" +

                    "{\n" +
                    "  \"title\": \"특수문자 없이 공백 포함 10자 이내 제목\",\n" +
                    "  \"content\": \"공백 포함 250자 이내로 구성된 구체적인 하루 기록. 예: 3월 14일은 졸업식이었어요. 교정에 앉아 사진을 찍던 장면이 기억에 남는 하루였어요. 같은 자연스럽고 회상하는 말투\"\n" +
                    "}";


    public String callHcx005(String base64Image) {
        Map<String, Object> dataUri = new HashMap<>();
        dataUri.put("data", "data:image/jpeg;base64," + base64Image);

        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("type", "image_url");
        imageContent.put("imageUrl", null);
        imageContent.put("dataUri", dataUri);

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", List.of(Map.of("type", "text", "text", SYSTEM_PROMPT_005))));
        messages.add(Map.of("role", "user", "content", List.of(
                Map.of("type", "text", "text", ""),
                imageContent
        )));

        return sendRequest(hcxUrl, messages, 0.5);
    }

    public AiResult callHcxDash002(String combinedAnalysis, String title, LocalDate date) {
        String userText = String.format("이벤트 제목: %s\n이벤트 날짜: %s\n\n4장의 사진 기록:\n%s",
                title, date, combinedAnalysis);

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system",
                "content", List.of(Map.of("type", "text", "text", SYSTEM_PROMPT_DASH))));
        messages.add(Map.of("role", "user",
                "content", List.of(Map.of("type", "text", "text", userText))));

        String response = sendRequest(dashUrl, messages, 0.5);

        try {
            String jsonOnly = extractJson(response);
            JsonNode node = objectMapper.readTree(jsonOnly);

            if (!node.has("title") || !node.has("content")) {
                throw new BusinessException(ErrorCode.AI_PARSING_FAILED);
            }

            String aiTitle = node.get("title").asText();
            String aiContent = node.get("content").asText();

            return new AiResult(aiTitle, aiContent);

        } catch (Exception e) {
            log.error("AI 응답 JSON 파싱 실패. 원본 응답: {}", response);
            throw new BusinessException(ErrorCode.AI_PARSING_FAILED);
        }
    }

    private String sendRequest(String url, List<Map<String, Object>> messages, double temperature) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", UUID.randomUUID().toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("messages", messages);
        body.put("topP", 0.8);
        body.put("topK", 0);
        body.put("maxTokens", 512);
        body.put("temperature", temperature);
        body.put("repetitionPenalty", 1.1);
        body.put("includeAiFilters", true);

        try {
            ResponseEntity<Map> resp =
                    restTemplate.postForEntity(url, new HttpEntity<>(body, headers), Map.class);

            Map<String, Object> responseBody = resp.getBody();

            if (responseBody == null) {
                throw new BusinessException(ErrorCode.CLOVA_RESPONSE_EMPTY);
            }

            Map<String, Object> status = (Map<String, Object>) responseBody.get("status");
            if (status == null || !"20000".equals(String.valueOf(status.get("code")))) {
                log.error("Clova API Error Response: {}", responseBody);
                throw new BusinessException(ErrorCode.CLOVA_API_ERROR);
            }

            Map<String, Object> result = (Map<String, Object>) responseBody.get("result");
            Map<String, Object> message = (Map<String, Object>) result.get("message");

            return (String) message.get("content");
        } catch (BusinessException e) {
            // 1. 이미 구체적으로 정의된 비즈니스 예외는 그대로
            throw e;
        } catch (Exception e) {
            // 2. 그 외의 알 수 없는 런타임 예외들만 일반적인 API 에러로 반환.
            log.error("Clova API 호출 중 예상치 못한 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.CLOVA_API_ERROR);
        }
    }

    private String extractJson(String response) {
        response = response.trim();

        // ```json ... ``` 제거
        if (response.startsWith("```")) {
            int firstBrace = response.indexOf("{");
            int lastBrace = response.lastIndexOf("}");
            if (firstBrace != -1 && lastBrace != -1) {
                return response.substring(firstBrace, lastBrace + 1);
            }
        }
        return response; // 이미 JSON이면 그대로
    }

}