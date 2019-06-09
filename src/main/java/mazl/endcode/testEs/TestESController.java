package mazl.endcode.testEs;

import mazl.endcode.TextParsing.FilmEntityRepository;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class TestESController {

    @Autowired
    FilmEntityRepository filmEntityRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @RequestMapping("testEsInsert")
    @ResponseBody
    public String testEsInsert(HttpServletRequest request) {
        elasticsearchTemplate.putMapping(FilmEntity.class);
        FilmEntity filmEntity = new FilmEntity();
        filmEntity.setName("马凡俗");
        filmEntity.setId(1L);
        filmEntity.setNameOri("mazl");
        filmEntity.setDirector("六天竺");
        filmEntityRepository.save(filmEntity);
        FilmEntity filmEntity1 = new FilmEntity();
        filmEntity1.setName("啊啊啊");
        filmEntity1.setId(2L);
        filmEntity1.setNameOri("mazl");
        filmEntity1.setDirector("六天竺");
        filmEntityRepository.save(filmEntity1);
        FilmEntity filmEntity2 = new FilmEntity();
        filmEntity2.setName("啊啊啊");
        filmEntity2.setId(3L);
        filmEntity2.setNameOri("mazl");
        filmEntity2.setDirector("六天竺");
        filmEntityRepository.save(filmEntity2);

        return "success";
    }
    @RequestMapping("testEsSearch")
    @ResponseBody
    public List<FilmEntity> testEsSearch(HttpServletRequest request) {

        //使用中文拼音混合搜索，取分数最高的，具体评分规则可参照：
        //  https://blog.csdn.net/paditang/article/details/79098830
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(structureQuery(request.getParameter("name")))
                .build();
        List<FilmEntity> list = filmEntityRepository.search(searchQuery).getContent();
        return list;
    }

    @RequestMapping("testEsHighlight")
    @ResponseBody
    public List<FilmEntity> testEsHighlight(HttpServletRequest request){
        Client client = elasticsearchTemplate.getClient();
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //高亮显示规则
        highlightBuilder.preTags("<span style='color:green'>");
        highlightBuilder.postTags("</span>");
        //指定高亮字段
        highlightBuilder.field("name");
        highlightBuilder.field("name.pinyin");
        highlightBuilder.field("director");
        String[] fileds = {"name", "name.pinyin", "director"};
        QueryBuilder matchQuery = QueryBuilders.multiMatchQuery(request.getParameter("param"), fileds);
        //搜索数据
        SearchResponse response = client.prepareSearch("film-entity")
                .setQuery(matchQuery)
                .highlighter(highlightBuilder)
                .execute().actionGet();

        SearchHits searchHits = response.getHits();
        System.out.println("记录数-->" + searchHits.getTotalHits());

        List<FilmEntity> list = new ArrayList<>();

        for (SearchHit hit : searchHits) {
            FilmEntity entity = new FilmEntity();
            Map<String, Object> entityMap = hit.getSourceAsMap();
            System.out.println(hit.getHighlightFields());
            //高亮字段
            if (!StringUtils.isEmpty(hit.getHighlightFields().get("name"))) {
                Text[] text = hit.getHighlightFields().get("name").getFragments();
                entity.setName(text[0].toString());
                entity.setDirector(String.valueOf(entityMap.get("director")));
            }
            if (!StringUtils.isEmpty(hit.getHighlightFields().get("name.pinyin"))) {
                Text[] text = hit.getHighlightFields().get("name.pinyin").getFragments();
                entity.setName(text[0].toString());
                entity.setDirector(String.valueOf(entityMap.get("director")));
            }
            if (!StringUtils.isEmpty(hit.getHighlightFields().get("director"))) {
                Text[] text = hit.getHighlightFields().get("director").getFragments();
                entity.setDirector(text[0].toString());
                entity.setName(String.valueOf(entityMap.get("name")));
            }

            //map to object
            if (!CollectionUtils.isEmpty(entityMap)) {
                if (!StringUtils.isEmpty(entityMap.get("id"))) {
                    entity.setId(Long.valueOf(String.valueOf(entityMap.get("id"))));
                }
                if (!StringUtils.isEmpty(entityMap.get("language"))) {
                    entity.setLanguage(String.valueOf(entityMap.get("language")));
                }
            }
            list.add(entity);
        }
        return list;
    }

    /**
     * 中文、拼音混合搜索
     *
     * @param content the content
     * @return dis max query builder
     */
    public DisMaxQueryBuilder structureQuery(String content) {
        //使用dis_max直接取多个query中，分数最高的那一个query的分数即可
        DisMaxQueryBuilder disMaxQueryBuilder = QueryBuilders.disMaxQuery();
        //boost 设置权重,只搜索匹配name和disrector字段
        QueryBuilder ikNameQuery = QueryBuilders.matchQuery("name", content).boost(2f);
        QueryBuilder pinyinNameQuery = QueryBuilders.matchQuery("name.pinyin", content).boost(4f);
        QueryBuilder ikDirectorQuery = QueryBuilders.matchQuery("director", content).boost(2f);
        disMaxQueryBuilder.add(ikNameQuery);
        disMaxQueryBuilder.add(pinyinNameQuery);
        disMaxQueryBuilder.add(ikDirectorQuery);
        return disMaxQueryBuilder;
    }

}
