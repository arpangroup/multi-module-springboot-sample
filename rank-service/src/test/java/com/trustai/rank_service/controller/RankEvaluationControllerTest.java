/*
package com.trustai.rank_service.controller;

import com.trustai.rank_service.dto.RankEvaluationResultDTO;
import com.trustai.rank_service.service.RankEvaluatorServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RankEvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RankEvaluatorServiceImpl rankEvaluatorService;

    @Test
    void evaluateUserRank_shouldReturnOk() throws Exception {
        Long userId = 1L;
        RankEvaluationResultDTO result = new RankEvaluationResultDTO(userId, "RANK_1", "RANK_2", true, "Upgraded");

        when(rankEvaluatorService.evaluateAndUpdateRank(userId)).thenReturn(result);

        mockMvc.perform(post("/api/v1/rankings/re-evaluate/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));
    }
}
*/
