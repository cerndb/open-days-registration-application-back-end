//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.ModelsDAO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "RESERVATION",
        indexes = {
            @Index(
                    name = "INDEX_REGISTRATION_ON_RESERVATION",
                    columnList = "ID_REGISTRATION")
        })
public class ReservationModelDAO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RESERVATION", nullable = false, updatable = false)
    private Long idReservation;

    @NotNull
    @Column(name = "ID_REGISTRATION")
    private Long idRegistration;

    @Column(name = "VISIT_DAY")
    private LocalDateTime visitDay;

    @Column(name = "ID_ARRIVAL_POINT")
    private Integer idArrivalPoint;

    @Column(name = "TIMESLOT_START")
    private LocalDateTime timeslotStart;

    @NotNull
    @Column(name = "NUMBER_OF_RESERVED_TICKETS")
    private Integer numberOfReservedTickets;

    @NotNull
    @Column(name = "NUMBER_OF_ADULT_TICKETS")
    private Integer numberOfAdultTickets;

    @Column(name = "NUMBER_OF_CHILD_TICKETS")
    private Integer numberOfChildTickets;

    @Column(name = "NUMBER_OF_FAST_TRACK_TICKETS")
    private Integer numberOfFastTrackTickets;

    @Column(name = "RESERVATION_STATUS")
    private Integer reservationStatus;

    @Column(name = "BARCODE")
    private String barcode;

    @Column(name = "HAS_REDUCED_MOBILITY")
    private Boolean hasReducedMobility;

    @Column(name = "ID_POINT_OF_ORIGIN")
    private Integer idPointOfOrigin;

    @NotNull
    @Column(name = "CHANGE_DATE")
    private LocalDateTime changeDate;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumns(
            value = {
                @JoinColumn(
                        name = "VISIT_DAY",
                        referencedColumnName = "OPEN_DAY",
                        insertable = false,
                        updatable = false)
                ,
        @JoinColumn(
                        name = "ID_ARRIVAL_POINT",
                        referencedColumnName = "ID_ARRIVAL_POINT",
                        insertable = false,
                        updatable = false)
                ,
        @JoinColumn(
                        name = "TIMESLOT_START",
                        referencedColumnName = "TIMESLOT_START",
                        insertable = false,
                        updatable = false)
            }, foreignKey = @ForeignKey(name = "FK_RESERVATION_ARRIVAL_POINT_OPENINGHOURS"))
    private ArrivalPointOpeningHourModelDAO arrivalPointOpeningTimes;

    @OneToOne
    @JoinColumn(
            name = "ID_POINT_OF_ORIGIN",
            referencedColumnName = "ID_POINT_OF_ORIGIN",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "FK_RESERVATION_POINT_OF_ORIGIN")
    )
    private PointOfOriginModelDAO pointOfOrigin;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(
            name = "ID_ARRIVAL_POINT",
            referencedColumnName = "ID_ARRIVAL_POINT",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "FK_RESERVATION_ARRIVAL_POINT"))
    private ArrivalPointModelDAO arrivalPoint;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ID_RESERVATION",
            referencedColumnName = "ID_RESERVATION"
    )
    private List<VisitorTransportTypeModelDAO> transportTypes;

    public ReservationModelDAO() {
    }

    public Long getIdReservation() {
        return idReservation;
    }

    public ReservationModelDAO setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
        return this;
    }

    public Long getIdRegistration() {
        return idRegistration;
    }

    public ReservationModelDAO setIdRegistration(Long idRegistration) {
        this.idRegistration = idRegistration;
        return this;
    }

    public LocalDateTime getVisitDay() {
        return visitDay;
    }

    public ReservationModelDAO setVisitDay(LocalDateTime visitDay) {
        this.visitDay = visitDay;
        return this;
    }

    public Integer getIdArrivalPoint() {
        return idArrivalPoint;
    }

    public ReservationModelDAO setIdArrivalPoint(Integer idArrivalPoint) {
        this.idArrivalPoint = idArrivalPoint;
        return this;
    }

    public LocalDateTime getTimeslotStart() {
        return timeslotStart;
    }

    public ReservationModelDAO setTimeslotStart(LocalDateTime timeslotStart) {
        this.timeslotStart = timeslotStart;
        return this;
    }

    public Integer getNumberOfReservedTickets() {
        return numberOfReservedTickets;
    }

    public ReservationModelDAO setNumberOfReservedTickets(Integer numberOfReservedTickets) {
        this.numberOfReservedTickets = numberOfReservedTickets;
        return this;
    }

    public Integer getNumberOfAdultTickets() {
        return numberOfAdultTickets;
    }

    public ReservationModelDAO setNumberOfAdultTickets(Integer numberOfAdultTickets) {
        this.numberOfAdultTickets = numberOfAdultTickets;
        return this;
    }

    public Integer getNumberOfChildTickets() {
        return numberOfChildTickets;
    }

    public ReservationModelDAO setNumberOfChildTickets(Integer numberOfChildTickets) {
        this.numberOfChildTickets = numberOfChildTickets;
        return this;
    }

    public Integer getNumberOfFastTrackTickets() {
        return numberOfFastTrackTickets;
    }

    public ReservationModelDAO setNumberOfFastTrackTickets(Integer numberOfFastTrackTickets) {
        this.numberOfFastTrackTickets = numberOfFastTrackTickets;
        return this;
    }

    public Integer getReservationStatus() {
        return reservationStatus;
    }

    public ReservationModelDAO setReservationStatus(Integer reservationStatus) {
        this.reservationStatus = reservationStatus;
        return this;
    }

    public String getBarcode() {
        return barcode;
    }

    public ReservationModelDAO setBarcode(String barcode) {
        this.barcode = barcode;
        return this;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public ReservationModelDAO setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
        return this;
    }

    public ArrivalPointOpeningHourModelDAO getArrivalPointOpeningTimes() {
        return arrivalPointOpeningTimes;
    }

    public ReservationModelDAO setArrivalPointOpeningTimes(ArrivalPointOpeningHourModelDAO arrivalPointOpeningTimes) {
        this.arrivalPointOpeningTimes = arrivalPointOpeningTimes;
        return this;
    }

    public ArrivalPointModelDAO getArrivalPoint() {
        return arrivalPoint;
    }

    public ReservationModelDAO setArrivalPoint(ArrivalPointModelDAO arrivalPoint) {
        this.arrivalPoint = arrivalPoint;
        return this;
    }

    public List<VisitorTransportTypeModelDAO> getTransportTypes() {
        return transportTypes;
    }

    public ReservationModelDAO setTransportTypes(List<VisitorTransportTypeModelDAO> transportTypes) {
        this.transportTypes = transportTypes;
        return this;
    }

    public Boolean getHasReducedMobility() {
        return hasReducedMobility;
    }

    public ReservationModelDAO setHasReducedMobility(Boolean hasReducedMobility) {
        this.hasReducedMobility = hasReducedMobility;
        return this;
    }

    public void increaseNumeberOfChildTickets(){
        this.numberOfChildTickets++;
    }

    public void increaseNumberOfAdultTickets(){
        this.numberOfAdultTickets++;
    }

    public void increaseNumberOfFastTrackTickets(){
        this.numberOfFastTrackTickets++;
    }

    public void increaseNumberOfReservedTickets(){
        this.numberOfReservedTickets++;
    }

    public Integer getIdPointOfOrigin() {
        return idPointOfOrigin;
    }

    public ReservationModelDAO setIdPointOfOrigin(Integer idPointOfOrigin) {
        this.idPointOfOrigin = idPointOfOrigin;
        return this;
    }

    public PointOfOriginModelDAO getPointOfOrigin() {
        return pointOfOrigin;
    }

    public ReservationModelDAO setPointOfOrigin(PointOfOriginModelDAO pointOfOrigin) {
        this.pointOfOrigin = pointOfOrigin;
        return this;
    }

}
