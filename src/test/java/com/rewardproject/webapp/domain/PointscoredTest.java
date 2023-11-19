package com.rewardproject.webapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.rewardproject.webapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PointscoredTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pointscored.class);
        Pointscored pointscored1 = new Pointscored();
        pointscored1.setId(1L);
        Pointscored pointscored2 = new Pointscored();
        pointscored2.setId(pointscored1.getId());
        assertThat(pointscored1).isEqualTo(pointscored2);
        pointscored2.setId(2L);
        assertThat(pointscored1).isNotEqualTo(pointscored2);
        pointscored1.setId(null);
        assertThat(pointscored1).isNotEqualTo(pointscored2);
    }
}
