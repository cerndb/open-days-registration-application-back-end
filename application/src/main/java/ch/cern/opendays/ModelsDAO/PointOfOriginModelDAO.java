//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.ModelsDAO;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "POINT_OF_ORIGIN")
public class PointOfOriginModelDAO implements Serializable {

    @Id
    @Column(name = "ID_POINT_OF_ORIGIN")
    private Integer idPointOfOrigin;

    @NotNull
    @Column(name = "POINT_OF_ORIGIN_NAME_EN")
    private String pointOfOriginNameEN;

    @NotNull
    @Column(name = "POINT_OF_ORIGIN_NAME_FR")
    private String pointOfOriginNameFR;

    public PointOfOriginModelDAO() {
    }

    public Integer getIdPointOfOrigin() {
        return idPointOfOrigin;
    }

    public PointOfOriginModelDAO setIdPointOfOrigin(Integer idPointOfOrigin) {
        this.idPointOfOrigin = idPointOfOrigin;
        return this;
    }

    public String getPointOfOriginNameEN() {
        return pointOfOriginNameEN;
    }

    public PointOfOriginModelDAO setPointOfOriginNameEN(String pointOfOriginNameEN) {
        this.pointOfOriginNameEN = pointOfOriginNameEN;
        return this;
    }

    public String getPointOfOriginNameFR() {
        return pointOfOriginNameFR;
    }

    public PointOfOriginModelDAO setPointOfOriginNameFR(String pointOfOriginNameFR) {
        this.pointOfOriginNameFR = pointOfOriginNameFR;
        return this;
    }

}
