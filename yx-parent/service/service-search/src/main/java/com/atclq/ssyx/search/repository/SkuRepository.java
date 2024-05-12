package com.atclq.ssyx.search.repository;

import com.atclq.ssyx.model.search.SkuEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//使用SpringData操作ES
public interface SkuRepository extends ElasticsearchRepository<SkuEs, Long> {
    //TODO:在这里只需要定义出来按照 SpringData方法命名规范 命名的方法即可，方法的实现由SpringData自动生成

    Page<SkuEs> findByOrderByHotScoreDesc(Pageable pageable);

    Page<SkuEs> findByWareIdAndCategoryId(Long wareId, Long categoryId, Pageable pageable);

    Page<SkuEs> findByWareIdAndCategoryIdAndKeyword(Long wareId, Long categoryId, String keyword, Pageable pageable);
}
