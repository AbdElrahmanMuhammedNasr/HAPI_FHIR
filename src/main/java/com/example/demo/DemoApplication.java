package com.example.demo;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootApplication
@Configuration
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public Jackson2ObjectMapperBuilder jacksonBuilder() {
		Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
		b.indentOutput(true).mixIn(org.hl7.fhir.r4.model.Reference.class, ReferenceMixin.class);
		return b;
	}

	public interface ReferenceMixin {
		@JsonIgnore
		void setReferenceElement(org.hl7.fhir.r4.model.StringType reference);

	}

	@Bean
	public IGenericClient getClient(){
		String baseUrl = "https://hapi.fhir.org/baseR4/";
		@SuppressWarnings("deprecation")
		FhirContext fhirContext = FhirContext.forR4();
		IGenericClient client = fhirContext.newRestfulGenericClient(baseUrl);
		return  client;
	}

}
