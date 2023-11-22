package model;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringExclude;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Property {
    private int id;
    private String street;
    private String city;
    private int listingNum;
    private int styleId;
    private int typeId;
    private int bedrooms;
    private float bathrooms;
    private int squarefeet;
    private String berRating;
    @ToStringExclude
    private String description;
    private String lotsize;
    private byte garagesize; // tinyint maps to byte in Java
    private int garageId;
    private int agentId;
    private String photo;
    private double price;
    private Date dateAdded;

    // Foreign keys are represented as objects
    private Style style;
    private PropertyType propertyType;
    private GarageType garageType;
    private Agent agent;

}
