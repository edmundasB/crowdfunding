package com.profitus.crowdfunding.rest

import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@Ignore
@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class InterestControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    @Ignore
    fun `when post investor interest when file uploaded` () {
        val investorsFile = MockMultipartFile("investors", "test.txt", "text/plain", "Spring Framework".toByteArray())
       // val loanFile = MockMultipartFile("loan", "loanfile.txt", "text/plain", "Spring Framework".toByteArray())
        this.mockMvc.perform(multipart("/v1/interest/file/")
                .file("investors", investorsFile.bytes)
                .file("loan", investorsFile.bytes))
                .andExpect(status().isOk)
    }

}