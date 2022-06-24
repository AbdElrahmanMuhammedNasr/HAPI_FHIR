package com.example.demo.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.example.demo.Model.CustomPatient;
import org.codehaus.jackson.map.ObjectMapper;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;


@Service
public class PatientService {
    @Autowired
    private IGenericClient getClient ;


    public Object getOnePatientUsing(String id) throws IOException {
        /**
         * get patient form FHIR
         *  and store it  in patient
         * **/
        Patient patient = getClient
                .read()
                .resource(Patient.class)
                .withId(id)
                .execute();

        /**
         *  convert patient to  string
         *
         *  **/
        String result_as_String = FhirContext.forR4().newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);

        /**
         *  convert String to json
         *  and send it to Controller
         *  **/
        Object patient_mapper = new ObjectMapper().readValue(result_as_String,Object.class);
        return  patient_mapper;
    }


    public Object getOnePatientUsingIdSHORT(String id) throws IOException {
      /**
       * same
       *    return short DATA (id , name)
       * */
        Patient patient = getClient
                .read()
                .resource(Patient.class)
                .withId(id)
                .elementsSubset("id","name") // only get id and name
                .execute();

        String result_as_String = FhirContext.forR4().newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);
        Object patient_mapper = new ObjectMapper().readValue(result_as_String,Object.class);
        return  patient_mapper;
    }

    public Object getPatientUsingName(String name) throws Exception{
        /**
         * get collection of patient AS Bundle
         * **/
        Bundle bundle = getClient
                .search()
                .forResource(Patient.class)
                .where(Patient.NAME.matches().value(name))
                .returnBundle(Bundle.class)
                .execute();

        /**
         * return bundle  (like container entry contain the data)
         * */
        String result_as_String = FhirContext.forR4().newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
        Object patients_mapper = new ObjectMapper().readValue(result_as_String,Object.class);

        return  patients_mapper;
    }

    public Object getPatientUsingNameAndAddress(String name, String city) throws Exception{
        /**
         * get collection of patient AS Bundle
         * **/
        Bundle bundle = getClient
                .search()
                .forResource(Patient.class)
                .where(Patient.NAME.matches().value(name))
                .and(Patient.ADDRESS_CITY.matches().value(city))
                .sort().ascending(Patient.BIRTHDATE)
                .elementsSubset("entry")
                .returnBundle(Bundle.class)
                .execute();

        /**
         * return bundle  (like container entry contain the data)
         * */
        String result_as_String = FhirContext.forR4().newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
        Object patients_mapper = new ObjectMapper().readValue(result_as_String,Object.class);

        return  patients_mapper;
    }

    public Boolean createPatient(CustomPatient customPatient){
        /**
         *  get data from custom patient from controller
         *  and set in Patient FHIR
         * */
        Patient patient = new Patient();
        patient.addIdentifier().setSystem("urn:system").setValue("12345");
        patient.addName().setFamily(customPatient.getLastName()).addGiven(customPatient.getFirstName());
        patient.setGender(customPatient.getGender().toLowerCase().equals("male") ? Enumerations.AdministrativeGender.MALE : Enumerations.AdministrativeGender.FEMALE);
//        patient.setBirthDate( new Date(String.valueOf(customPatient.getBirthDate())));
//        patient.setAddress();
        /**
         * create patient
         * */
        MethodOutcome methodOutcome = getClient.create()
                .resource(patient)
                .prettyPrint()
                .encodedJson()
                .execute();

        IIdType id = methodOutcome.getId();
        System.out.println("Got ID------------------->: " + id.getValue());
        System.out.println("is Done ------------------->: " +  methodOutcome.getCreated());
        return methodOutcome.getCreated(); /** return ture of created */
    }

    public void deletePatient(String patientId){
        /**
         * get patient id
         * delete patient using id
         * we pass resources and patient id
         * **/
        MethodOutcome methodOutcome =
                getClient.delete()
                .resourceById(new IdType("Patient",patientId))
                .execute();

        IIdType id = methodOutcome.getId();
        System.out.println("delete ID------------------->: " + id.getValue());

    }


    public void updatePatient(String patientId){
        /**
         * get patient id
         * update patient using id
         * **/
        Patient patient = new Patient();
        patient.addName().setFamily("AAAli").addGiven("TTTAmer Bin");
        patient.setId("Patient/"+patientId);

        MethodOutcome methodOutcome =
                getClient.update()
                        .resource(patient)
                        .execute();
        IIdType id = methodOutcome.getId();
        System.out.println("update ID------------------->: " + id.getValue());

    }

    /**
     *
     * i will search for encounter using patient name
     *  encounter have only patient ID
     *   we will use chaining
     * */

    public Object getEncounterUsingPatientNameAndCity(String name, String city) throws Exception {
        Bundle bundle =
                getClient
                        .search()
//                        .forResource(Patient.class)
//                        .where(Patient.BIRTHDATE.beforeOrEquals().day("2011-01-01"))
//                        .and(Patient.GENERAL_PRACTITIONER.hasChainedProperty(Organization.NAME.matches().value("Smith")))
//                        .returnBundle(Bundle.class)
//                        .execute();

                        .forResource(Encounter.class)
                        .where(Encounter.PATIENT.hasChainedProperty(Patient.NAME.matches().value(name)))
                        .and(Encounter.PATIENT.hasChainedProperty(Patient.ADDRESS.matches().value(city)))
                        .returnBundle(Bundle.class)
                        .execute();


        /**
         *  convert patient to  string
         *
         *  **/
        String result_as_String = FhirContext.forR4().newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);

        /**
         *  convert String to json
         *  and send it to Controller
         *  **/
        Object bundle_mapper = new ObjectMapper().readValue(result_as_String,Object.class);

        return bundle_mapper;


    }
    /**
     *
     * i will search for encounter using patient name
     *  encounter have only patient ID and return also th patient
     *   we will use chaining and include
     * */
    public Object getEncounterUsingPatientNameAndCityAndIncludePatient(String name, String city) throws Exception {
        Bundle bundle =
                getClient
                        .search()
                        .forResource(Encounter.class)
                        .where(Encounter.PATIENT.hasChainedProperty(Patient.NAME.matches().value(name)))
                        .and(Encounter.PATIENT.hasChainedProperty(Patient.ADDRESS.matches().value(city)))
                        /**
                         * this line to include th patient
                         * */
                        .include(Encounter.INCLUDE_PATIENT.asRecursive())
                        .returnBundle(Bundle.class)
                        .execute();


        /**
         *  convert patient to  string
         *
         *  **/
        String result_as_String = FhirContext.forR4().newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);

        /**
         *  convert String to json
         *  and send it to Controller
         *  **/
        Object bundle_mapper = new ObjectMapper().readValue(result_as_String,Object.class);

        return bundle_mapper;


    }

    /**
     * get patient where encounter status is finish
     *
     * note :
     *  reverse chaining not supported by one of the existing fluent methods  according  the HAPI website
     *  so we use the url
     * */

    public Object getPatientWhereEncounterStatus(String status) throws Exception {
        String url = "https://hapi.fhir.org/baseR4/Patient?_has:Encounter:patient:status="+status;
        Bundle bundle =
                getClient
                        .search()
                        .byUrl(url)
                        .returnBundle(Bundle.class)
                        .execute();


        /**
         *  convert patient to  string
         *  **/
        String result_as_String = FhirContext.forR4().newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);

        /**
         *  convert String to json
         *  and send it to Controller
         *  **/
        Object bundle_mapper = new ObjectMapper().readValue(result_as_String,Object.class);

        return bundle_mapper;


    }
    }
