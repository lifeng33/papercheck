package com.fengcloud.papercheck;

import com.fengcloud.papercheck.model.CheckInfo;
import com.fengcloud.papercheck.service.CheckService;
import com.fengcloud.papercheck.service.ReportService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PapercheckApplicationTests {

    @Resource
    private CheckService checkService;
    @Resource
    private ReportService reportService;

    @Test
    public void testCheck() {
        //String query= "要回答数学教师信念是什么，研究者一般会从阐述信念是什么入手。通过对相关国内外研究文献的梳理和归纳,本文将新媒体对于旅游者行为的影响分为旅游信息搜索、旅游决策、旅游体验、游后分享评论等四个方面进行综述;对于信念的定义目前仍然不统一，而且信念常常容易与观念、信仰甚至知识混淆在一起，有研究对此做了区分[2][6]。一般而言，信念是指人们心理上所持有的相信是真实的理解、前提或主张[7]。数学教师信念是数学教师持有的信念，一般特指与数学、数学的教与学等有关的思想和观点[4]。认识信念包含于信念之中，即认识信念是信念的子集，它特指个体对知识的基本特征以及知识获得过程的基本特征做作的主观判断[8]。喻平认为，如果把问题框定在教育领域，对知识本质的认识以及对知识获取的本质的认识，既与学生有关也与教师有关，因此教师教学认识信念指教师对知识本质特征、对学生获得知识过程基本特征、对教学本质的过程等三方面的认识所形成的一个相对稳定的、带有一定意动成分的基本观点、态度和心理倾向[8]。通过对相关国内外研究文献的梳理和归纳,本文将新媒体对于旅游者行为的影响分为旅游信息搜索、旅游决策、旅游体验、游后分享评论等四个方面进行综述;由于信念与认识信念的区别，数学教师信念与数学教师认识信念本也有着不同的内涵，但在国内研究文献中，当具体到数学教师时，信念的含义窄化，认识信念的含义泛化，结果是两者所表达的内涵已经非常接近，均主要指数学教师对数学、数学教与学的认识。";
        //String query = "对于园林绿化企业来说，不管是在初期的招投标环节、还是在最后的竣工结算环节，成本控制始终都是贯穿其中的，应引起高度重视。不过结合目前园林绿化企业运营情况分析，在各阶段工作开展中，存在较多问题，成本管控无法有效落实，增大了企业财务风险系数。在投标环节，一些企业因为专业能力不足，在工程预算编制上存在较多漏洞，甚至为了获得竞标项目，故意压低报价，这使得后期工程施工存在较多问题，为企业发展埋下了较大的财务风险隐患。在施工单位的选择上，由于园林绿化工程的综合性相对较强，包含的专业内容较多，如景观、土石方、水景工程的，所以对于施工队伍的专业要求也较为严格。不过现阶段施工部门普遍存在专业技术水平较差，技能应用范围较窄的情况，这就增大了工程建设的风险系数。在施工过程中，过于重视苗木栽植和养护，忽略了整体效果的完善，这使得工程建设实际情况与设计内容存在较大偏差，增加了成本支出。另外，企业对于直接和间接成本的管控力度不严，甚至还存在未将直接和间接成本纳入在成本控制中的情况，增加了财务风险的发生概率。";
        String query = "对于园林绿化企业来说，不管是在初期的招投标环节、还是在最后的竣工结算环节，成本控制始终都是贯穿其中的，应引起高度重视。";
        List<CheckInfo> infos = checkService.doCheck(query);
        //String zipPath = reportService.buildReport(infos);
    }
    @Test
    public void testGetHtml(){
        try {
            String encodeString = URLEncoder.encode("成本控制始终都是贯穿其中的", "utf-8");
            String url = "https://xueshu.baidu.com/s?ie=utf-8&f=8&wd="+encodeString;
            Document html = Jsoup.connect(url)
                    .validateTLSCertificates(false) //忽略证书认证
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                    .timeout(5000)
                    .get();
            System.out.println(html.html());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
