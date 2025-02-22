/*
 * Copyright 2016-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.rest;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.security.HasRoleVetAdmin;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Vitaliy Fedoriv
 *
 */

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("/api/vets")
public class VetRestController {

	@Autowired
	private ClinicService clinicService;

    @HasRoleVetAdmin
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<Vet>> getAllVets(){
        Collection<Vet> vets = new ArrayList<>(this.clinicService.findAllVets());
		if (vets.isEmpty()){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(vets, HttpStatus.OK);
	}

    @HasRoleVetAdmin
	@GetMapping(path = "/{vetId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Vet> getVet(@PathVariable("vetId") int vetId){
		Vet vet = this.clinicService.findVetById(vetId);
		if(vet == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(vet, HttpStatus.OK);
	}

    @HasRoleVetAdmin
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Vet> addVet(@RequestBody @Valid Vet vet, BindingResult bindingResult, UriComponentsBuilder ucBuilder){
		BindingErrorsResponse errors = new BindingErrorsResponse();
		HttpHeaders headers = new HttpHeaders();
		if(bindingResult.hasErrors() || (vet == null)){
			errors.addAllErrors(bindingResult);
			headers.add("errors", errors.toJSON());
			return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
		}
		this.clinicService.saveVet(vet);
		headers.setLocation(ucBuilder.path("/api/vets/{id}").buildAndExpand(vet.getId()).toUri());
		return new ResponseEntity<>(vet, headers, HttpStatus.CREATED);
	}

    @HasRoleVetAdmin
	@PutMapping(path = "/{vetId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Vet> updateVet(@PathVariable("vetId") int vetId, @RequestBody @Valid Vet vet, BindingResult bindingResult){
		BindingErrorsResponse errors = new BindingErrorsResponse();
		HttpHeaders headers = new HttpHeaders();
		if(bindingResult.hasErrors() || (vet == null)){
			errors.addAllErrors(bindingResult);
			headers.add("errors", errors.toJSON());
			return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
		}
		Vet currentVet = this.clinicService.findVetById(vetId);
		if(currentVet == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		currentVet.setFirstName(vet.getFirstName());
		currentVet.setLastName(vet.getLastName());
		currentVet.clearSpecialties();
		for(Specialty spec : vet.getSpecialties()) {
			currentVet.addSpecialty(spec);
		}
		this.clinicService.saveVet(currentVet);
		return new ResponseEntity<>(currentVet, HttpStatus.NO_CONTENT);
	}

    @HasRoleVetAdmin
	@DeleteMapping(path = "/{vetId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseEntity<Void> deleteVet(@PathVariable("vetId") int vetId){
		Vet vet = this.clinicService.findVetById(vetId);
		if(vet == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		this.clinicService.deleteVet(vet);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
