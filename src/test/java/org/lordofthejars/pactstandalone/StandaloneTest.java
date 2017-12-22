package org.lordofthejars.pactstandalone;

import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.Pact;
import au.com.dius.pact.model.PactReader;
import au.com.dius.pact.model.RequestResponseInteraction;
import au.com.dius.pact.model.RequestResponsePact;
import io.restassured.builder.RequestSpecBuilder;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

public class StandaloneTest {

    @Test
    public void should_upload_pact() {

        final Pact pact = PactReader.loadPact(new File("src/test/resources/contract.json"));

        final List<RequestResponseInteraction> collect = pact.getInteractions()
            .stream()
            .map(i -> (RequestResponseInteraction) i)
            .collect(Collectors.toList());

        RequestResponsePact requestResponsePact = new RequestResponsePact(pact.getProvider(), pact.getConsumer(), collect);

        MockProviderConfig config = MockProviderConfig.createDefault();
        runConsumerTest(requestResponsePact, config, mockServer -> {

            RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
            requestSpecBuilder.setBaseUri(mockServer.getUrl());

            given()
                .spec(requestSpecBuilder.build())
                .when()
                .get("/rest/planet/orbital/biggest")
                .then()
                .assertThat()
                .body("$", hasItems("Bespin", "Yavin IV", "Hoth"));

        });


    }

}
