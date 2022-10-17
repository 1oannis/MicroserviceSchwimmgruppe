package com.acme.schwimmgruppe.rest

import org.springframework.hateoas.EntityModel

data class SchwimmgruppeList(val schwimmgruppen: List<EntityModel<SchwimmgruppeDTO>>)

@SuppressWarnings("ConstructorParameterNaming")
data class SchwimmgruppenModel(val _embedded: SchwimmgruppeList)
