//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class PointOfOriginSelectionListModel {

    @JsonProperty("pointOfOriginSelectionList")
    public List<PointOfOriginDropDownElementModel> pointOfOriginSelectionList;

    @JsonProperty("selectedPointOfOrigin")
    public Integer selectedPointOfOrigin;

    public PointOfOriginSelectionListModel(){
        this.pointOfOriginSelectionList = new ArrayList<>();
    }

    public List<PointOfOriginDropDownElementModel> getPointOfOriginSelectionList() {
        return pointOfOriginSelectionList;
    }

    public PointOfOriginSelectionListModel setPointOfOriginSelectionList(List<PointOfOriginDropDownElementModel> pointOfOriginSelectionList) {
        this.pointOfOriginSelectionList = pointOfOriginSelectionList;
        return this;
    }

    public PointOfOriginSelectionListModel setSelectedPointOfOrigin(Integer selectedPointOfOrigin) {
        this.selectedPointOfOrigin = selectedPointOfOrigin;
        return this;
    }



}
