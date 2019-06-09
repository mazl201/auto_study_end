package mazl.endcode.TextParsing;

import mazl.endcode.testEs.FilmEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

public interface FilmEntityRepository extends ElasticsearchRepository<FilmEntity, Long> {
}
