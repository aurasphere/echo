package co.aurasphere.echo.rasa.publisher.strategy;

import java.util.Map;

import co.aurasphere.echo.rasa.publisher.model.rasa.nlu.NluData;

public interface PublishStrategy {

    void publish(Map<String, Map<String, String>> endpoints, Map<String, NluData> nlu, Map<String, Object> domain, String stories, String config) throws Exception;
}