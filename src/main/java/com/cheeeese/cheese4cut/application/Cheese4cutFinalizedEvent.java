package com.cheeeese.cheese4cut.application;

import com.cheeeese.album.domain.Album;
import com.cheeeese.cheese4cut.domain.Cheese4cut;
import com.cheeeese.photo.domain.Photo;

import java.util.List;

public record Cheese4cutFinalizedEvent(
        Cheese4cut cheese4cut,
        Album album,
        List<Photo> photos
) {}
