package com.bbva.arq.devops.ae.mirrorgate.repository;

import com.bbva.arq.devops.ae.mirrorgate.model.UserMetric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

public class UserMetricsRepositoryImpl implements UserMetricsRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<UserMetric> findAllStartingWithViewId(List<String> viewIds) {

        Aggregation agg = newAggregation(
            match(getCriteriaExpressionsForUserMetrics(viewIds))
        );

        AggregationResults<UserMetric> aggregationResult
            = mongoTemplate.aggregate(agg, UserMetric.class, UserMetric.class);

        return aggregationResult.getMappedResults();
    }

    private Criteria getCriteriaExpressionsForUserMetrics(List<String> viewIds) {
        return Criteria
            .where("viewId")
            .in(viewIds.stream().map(id -> Pattern.compile("^" + id + ".*$")).toArray());
    }

}
