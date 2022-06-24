package com.example.demo.Control;
;
import com.example.demo.Model.CustomPatient;
import com.example.demo.Service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController()
@CrossOrigin(origins = "*")
@RequestMapping("/patient")
public class main {


    @Autowired
    private PatientService patientService;

    /**
     * searching
     * */

    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getPatient(@PathVariable("id") String id ) throws Exception {
        return new ResponseEntity<>(patientService.getOnePatientUsing(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/id/{id}/short", method = RequestMethod.GET)
    public ResponseEntity<Object> getPatientShort(@PathVariable("id") String id ) throws Exception {
        return new ResponseEntity<>(patientService.getOnePatientUsingIdSHORT(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/name/{name}", method = RequestMethod.GET)
    public ResponseEntity<Object> getPatientUsingName(@PathVariable("name") String name) throws Exception {
        return new ResponseEntity<>(patientService.getPatientUsingName(name), HttpStatus.OK);
    }

    @RequestMapping(value = "/name-city/{name}/{city}", method = RequestMethod.GET)
    public ResponseEntity<Object> getPatientUsingNameAndCity(@PathVariable("name") String name, @PathVariable("city") String city) throws Exception {
        return new ResponseEntity<>(patientService.getPatientUsingNameAndAddress(name,city), HttpStatus.OK);
    }
    /**
     * end searching
     * */

    /**
     *
     * create patient
     * */
    @RequestMapping(value = "/create-patient", method = RequestMethod.POST)
    public ResponseEntity<Boolean> createPatient(@RequestBody CustomPatient customPatient){
        Boolean isCreated =  patientService.createPatient(customPatient);
        return  new ResponseEntity<>(isCreated,HttpStatus.OK);
    }

    /**
     *
     * delete patient using id
     * */
    @RequestMapping(value = "/delete-patient/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deletePatient(@PathVariable("id") String patientId){
         patientService.deletePatient(patientId);
        return  new ResponseEntity<>(null,HttpStatus.OK);
    }



    /**
     *
     * update patient
     * */
    @RequestMapping(value = "/update-patient/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Boolean> updatePatient(@PathVariable("id") String patientId){
                patientService.updatePatient(patientId);
        return  new ResponseEntity<>(null,HttpStatus.OK);
    }

    /**
     * using chaining
     * */

    @RequestMapping(value = "/encounter/{pName}/{pCity}", method = RequestMethod.GET)
    public ResponseEntity<Object> getEncounterUsingNameAndCity(@PathVariable("pName") String name, @PathVariable("pCity") String city) throws Exception {
        return new ResponseEntity<>(patientService.getEncounterUsingPatientNameAndCity(name,city), HttpStatus.OK);
    }

    /**
     * using chaining and include
     * */

    @RequestMapping(value = "/encounter-include/{pName}/{pCity}", method = RequestMethod.GET)
    public ResponseEntity<Object> getEncounterUsingNameAndCityAndInclude(@PathVariable("pName") String name, @PathVariable("pCity") String city) throws Exception {
        return new ResponseEntity<>(patientService.getEncounterUsingPatientNameAndCityAndIncludePatient(name,city), HttpStatus.OK);
    }

    /**
     * using reverse chaining
     * */
    @RequestMapping(value = "/reverse-encounter/{status}", method = RequestMethod.GET)
    public ResponseEntity<Object> getPatientWhenEncounterStatus(@PathVariable("status") String status) throws Exception {
        return new ResponseEntity<>(patientService.getPatientWhereEncounterStatus(status), HttpStatus.OK);
    }



}
