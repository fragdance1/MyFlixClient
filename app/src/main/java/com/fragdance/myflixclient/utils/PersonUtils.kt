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
        if(crew.movie?.title != null)crew.movie?.title!! else "No title",
        crew.job,
        crew.movie?.poster,
        crew.movie?.progress,
        crew.movie?.disc,
        crew.movie?.videofile,
        crew.movie?.watched
    )
}

fun castToMovieCard(cast: ICast): IMovieCardData {
    return IMovieCardData(
        cast.movie?.id.toString(),
        cast.movie?.title!!,
        cast.character,
        cast.movie?.poster,
        cast.movie?.progress,
        cast.movie?.disc == true || (cast.movie?.discs?.isNotEmpty() == true),
        cast.movie?.videofile == true ||(cast.movie?.video_files?.isNotEmpty()==true),
        cast.movie?.watched
    )
}

