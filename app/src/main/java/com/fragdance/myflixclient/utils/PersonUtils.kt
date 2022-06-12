package com.fragdance.myflixclient.utils

import com.fragdance.myflixclient.models.ICast
import com.fragdance.myflixclient.models.ICrew
import com.fragdance.myflixclient.models.IMovieCardData
import com.fragdance.myflixclient.models.IPersonCardData

fun castToPersonCard(cast: ICast): IPersonCardData {
    return IPersonCardData(
        cast.person.id.toString(),
        cast.person.name,
        cast.character,
        cast.person.portrait
    )
}

fun crewToPersonCard(crew: ICrew): IPersonCardData {
    return IPersonCardData(
        crew.person.id.toString(),
        crew.person.name,
        crew.job,
        crew.person.portrait
    )
}

fun crewToMovieCard(crew: ICrew): IMovieCardData {
    return IMovieCardData(
        crew.movie?.id.toString(),
        crew.movie?.title!!,
        crew.job,
        crew.movie?.poster,
        crew.movie?.progress
    )
}

fun castToMovieCard(cast: ICast): IMovieCardData {
    return IMovieCardData(
        cast.movie?.id.toString(),
        cast.movie?.title!!,
        cast.character,
        cast.movie?.poster,
        cast.movie?.progress
    )
}

