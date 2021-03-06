//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Models;

public class AvailablePlacesModel {

    private int availablePlaces;
    private int availableFastTrackPlaces;

    public AvailablePlacesModel() {
    }

    public int getAvailablePlaces() {
        return availablePlaces;
    }

    public AvailablePlacesModel setAvailablePlaces(int availablePlaces) {
        this.availablePlaces = availablePlaces;
        return this;
    }

    public int getAvailableFastTrackPlaces() {
        return availableFastTrackPlaces;
    }

    public AvailablePlacesModel setAvailableFastTrackPlaces(int availableFastTrackPlaces) {
        this.availableFastTrackPlaces = availableFastTrackPlaces;
        return this;
    }

}
