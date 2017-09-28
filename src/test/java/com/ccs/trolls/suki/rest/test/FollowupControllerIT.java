package com.ccs.trolls.suki.rest.test;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

//TODO：是否确实需要每个Test/IT.java都需要重启一遍应用？太慢。
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class FollowupControllerIT {
  private MockMvc mockMvc;

  @Autowired private WebApplicationContext webApplicationContext;

  @Before
  public void setup() throws Exception {
    this.mockMvc = webAppContextSetup(webApplicationContext).build();
  }

  @Test
  public void testFollowupFromFluppfAndHxclpf() throws Exception {
    String policyId = "01200941";
    mockMvc
        .perform(get("/api/v1/followup/{branchId}/{policyId}", "1", policyId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$.[0].branchId").value("1"))
        .andExpect(jsonPath("$.[0].policyId").value(policyId))
        .andExpect(jsonPath("$.[0].seq").value(84))
        .andExpect(jsonPath("$.[0].code.code").value("401"))
        //.andExpect(jsonPath("$.[0].code.name").value("特别约定"))
        .andExpect(jsonPath("$.[0].type.code").value("P"))
        //.andExpect(jsonPath("$.[0].type.name").value("建议书"))
        .andExpect(jsonPath("$.[0].followupDate").value("2001-05-05"))
        .andExpect(jsonPath("$.[0].remark").value("特别约定"))
        .andExpect(jsonPath("$.[0].status.code").value("O"))
        .andExpect(jsonPath("$.[0].status.name").isEmpty())
        .andExpect(jsonPath("$.[0].insuredNo").value("01"))
        .andExpect(jsonPath("$.[0].jointNo").value("00"))
        .andExpect(jsonPath("$.[0].transactionNo").value(1))
        .andExpect(jsonPath("$.[0].transactionDateTime").value("2001-05-05T14:17:48+08:00"))
        .andExpect(jsonPath("$.[0].claimId").value(""))
        .andExpect(jsonPath("$.[0].letterType.code").value("SZ00004"))
        //.andExpect(jsonPath("$.[0].letterType.name").value("小孩特约特别约定"))
        .andExpect(jsonPath("$.[0].autoFollow").value(false))
        .andExpect(
            jsonPath("$.[0].specialClause")
                .value(
                    "1, 在保单有效期间，若被保险人１８周岁前身故或高残，本公司累计给付死亡或高残保险金５万元，超出部分按退保处理；被保险人１８周岁（含）后，按保单所列明的保险金额承担保险责任．2, kjshfkjhdsfffff3, rewrrqwrewrqwrr"))
        .andExpect(jsonPath("$.[0].userCode").value("L1NBXQS"))
        .andExpect(jsonPath("$.[1].seq").value(85))
        .andDo(print()) //打印出请求和响应的内容
        .andReturn()
        .getResponse()
        .getContentAsString(); //将响应的数据转换为字符串
    ;
  }

  @Test
  public void testFollowupFromFluppfAndZhclpf() throws Exception {
    String policyId = "H0745219";
    mockMvc
        .perform(get("/api/v1/followup/{branchId}/{policyId}", "1", policyId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$", hasSize(8)))
        .andExpect(jsonPath("$.[0].branchId").value("1"))
        .andExpect(jsonPath("$.[0].policyId").value(policyId))
        .andExpect(jsonPath("$.[0].seq").value(23))
        .andExpect(jsonPath("$.[0].code.code").value("A01"))
        //.andExpect(jsonPath("$.[0].code.name").value("请作普检项目"))
        .andExpect(jsonPath("$.[0].type.code").value("T"))
        .andExpect(jsonPath("$.[0].type.name").isEmpty())
        .andExpect(jsonPath("$.[0].followupDate").value("2004-09-01"))
        .andExpect(jsonPath("$.[0].remark").value("请作普检项目"))

        //.andExpect(jsonPath("$.[0].status").isEmpty())
        .andExpect(jsonPath("$.[0].status.code").value(""))
        .andExpect(jsonPath("$.[0].status.name").value(""))
        .andExpect(jsonPath("$.[0].insuredNo").value("01"))
        .andExpect(jsonPath("$.[0].jointNo").value(""))
        .andExpect(jsonPath("$.[0].transactionNo").value(13))
        .andExpect(jsonPath("$.[0].transactionDateTime").value("2004-09-01T15:37:43+08:00"))
        .andExpect(jsonPath("$.[0].claimId").value(""))
        //.andExpect(jsonPath("$.[0].letterType").isEmpty())
        .andExpect(jsonPath("$.[0].letterType.code").value(""))
        .andExpect(jsonPath("$.[0].letterType.name").isEmpty())
        .andExpect(jsonPath("$.[0].autoFollow").value(false))
        .andExpect(jsonPath("$.[0].specialClause").value("0, 0, 0, 0, 0, 0, 0, 0, "))
        .andExpect(jsonPath("$.[0].userCode").value("L1HLGLS"))
        .andExpect(jsonPath("$.[1].seq").value(24))
        .andDo(print()) //打印出请求和响应的内容
        .andReturn()
        .getResponse()
        .getContentAsString(); //将响应的数据转换为字符串
    ;
  }
}
