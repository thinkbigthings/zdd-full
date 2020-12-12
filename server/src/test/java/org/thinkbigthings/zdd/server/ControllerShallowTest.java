package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


// With Junit 5, we do not need @RunWith(SpringRunner.class) anymore.
// Spring tests are executed with @ExtendWith(SpringExtension.class),
// and @SpringBootTest, and the other @...Test annotations are already annotated with it.

@Disabled("Disabled until we figure out how to test with mock mvc")
@Tag("integration")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class ControllerShallowTest {

    @Autowired MockMvc mvc;

    @Autowired
    private UserController controller;

    @Test
    @DisplayName("Basic Spring wiring")
    public void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Test
    void exampleTest() throws Exception {

        ResultActions results = mvc.perform(get("/users"));

        results.andExpect(status().isOk());

        MvcResult result = results.andReturn();
        assertNotNull(result);

//        mvc.perform(get("/"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Hello World"));
    }

}
