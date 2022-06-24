package org.gerimedica.exercise.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Table(name = "data")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CsvEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "SOURCE")
    private String source;
    @Column(name = "CODE_LIST_CODE")
    private String codeListCode;
    @Column(name = "CODE", unique = true)
    private String code;
    @Column(name = "DISPLAY_VALUE")
    private String displayValue;
    @Column(name = "LONG_DESCRIPTION")
    private String longDescription;
    @Column(name = "FROM_DATE")
    private Date fromDate;
    @Column(name = "TO_DATE")
    private Date toDate;
    @Column(name = "SORTING_PRIORITY")
    private Integer sortingPriority;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CsvEntity)) return false;

        CsvEntity that = (CsvEntity) o;

        return (!Objects.equals(id, that.id));
    }

    /**
     * read: <a href="https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/">more info</a>
     *
     * @return
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


