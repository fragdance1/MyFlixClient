package com.fragdance.myflixclient.models

import java.io.Serializable
data class ICrew(val person:IPerson,val job: String,val movie:IMovie?): Serializable
