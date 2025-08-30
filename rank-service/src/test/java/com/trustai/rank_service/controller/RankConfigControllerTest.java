/*
package com.trustai.rank_service.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustai.rank_service.dto.RankConfigDto;
import com.trustai.rank_service.entity.RankConfig;
import com.trustai.rank_service.exception.TestExceptionHandler;
import com.trustai.rank_service.service.RankConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestExceptionHandler.class)
public class RankConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RankConfigService rankConfigService;

    @Autowired
    private ObjectMapper objectMapper;

    */
/*@Test
    void getAllRanks_shouldReturnPagedResult() throws Exception {
        RankConfig mockRank = new RankConfig();
        mockRank.setId(1L);
        mockRank.setCode("R1");
        mockRank.setDisplayName("Rank 1");
        mockRank.setRankOrder(1);

        List<RankConfig> page = new PageImpl<>(List.of(mockRank));
        when(rankConfigService.getAllRankConfigs(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/rankings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].code").value("R1"));
    }*//*


    @Test
    void getAllRanks_shouldReturnListResult() throws Exception {
        RankConfig mockRank = new RankConfig();
        mockRank.setId(1L);
        mockRank.setCode("R1");
        mockRank.setDisplayName("Rank 1");
        mockRank.setRankOrder(1);

        List<RankConfig> mockList = List.of(mockRank);
        when(rankConfigService.getAllRankConfigs()).thenReturn(mockList);

        mockMvc.perform(get("/api/v1/rankings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("R1"))
                .andExpect(jsonPath("$[0].displayName").value("Rank 1"))
                .andExpect(jsonPath("$[0].rankOrder").value(1));
    }

    @Test
    void getRankById_shouldReturnRank() throws Exception {
        RankConfig sample = new RankConfig();
        sample.setId(1L);
        sample.setCode("RANK_1");

        when(rankConfigService.getRankById(1L)).thenReturn(sample);

        mockMvc.perform(get("/api/v1/rankings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("RANK_1"));
    }

    @Test
    void getRankByCode_shouldReturnRank() throws Exception {
        RankConfig sample = new RankConfig();
        sample.setId(1L);
        sample.setCode("RANK_2");

        when(rankConfigService.getRankByRankCode("RANK_2")).thenReturn(sample);

        mockMvc.perform(get("/api/v1/rankings/code/RANK_2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("RANK_2"));
    }

    @Test
    void createRank_shouldReturnOk() throws Exception {
        RankConfigDto dto = new RankConfigDto();
        dto.setCode("RANK_3");
        dto.setRankOrder(3);

        mockMvc.perform(post("/api/v1/rankings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(rankConfigService).createRank(eq(dto));
    }

    @Test
    void patchRank_shouldReturnOk() throws Exception {
        Map<String, Object> updates = Map.of("minDeposit", 1000);

        mockMvc.perform(patch("/api/v1/rankings/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk());

        verify(rankConfigService).patchRank(eq(1L), eq(updates));
    }

    @Test
    void patchMultipleRanks_shouldApplyAllPatches() throws Exception {
        List<Map<String, Object>> updatesList = List.of(
                Map.of("id", 1, "minDeposit", 1000),
                Map.of("id", 2, "minTeamSize", 10)
        );

        mockMvc.perform(patch("/api/v1/rankings/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatesList)))
                .andExpect(status().isOk());

        verify(rankConfigService).patchRank(eq(1L), eq(Map.of("minDeposit", 1000)));
        verify(rankConfigService).patchRank(eq(2L), eq(Map.of("minTeamSize", 10)));
    }

    @Test
    void patchMultipleRanks_shouldThrowOnMissingId() throws Exception {
        List<Map<String, Object>> updatesList = List.of(
                Map.of("minDeposit", 1000) // missing "id"
        );

        mockMvc.perform(patch("/api/v1/rankings/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatesList)))
                .andExpect(status().isInternalServerError()) // Expect 500
                .andExpect(content().string("Each update must include an 'id' field."));
    }
}
*/
