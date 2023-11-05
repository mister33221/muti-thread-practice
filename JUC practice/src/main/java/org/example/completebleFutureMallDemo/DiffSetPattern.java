package org.example.completebleFutureMallDemo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

public class DiffSetPattern {

    public static void main(String[] args) {
        Student student1 = new Student();
//        normal way to set value
        student1.setId(1);
        student1.setStudentName("Kai");
        student1.setMajor("CS");

//       use builder to set value
        Student student2 = Student.builder()
                .id(2)
                .studentName("John")
                .major("CS")
                .build();

//        chain way to set value
        Student student3 = new Student();
        student3.setId(3).setStudentName("Mary").setMajor("CS");

//        constructor way to set value
        Student student4 = new Student(4, "Tom", "CS");

        System.out.println(student1);
        System.out.println(student2);
        System.out.println(student3);
        System.out.println(student4);

    }


}

@Data
@AllArgsConstructor
@NoArgsConstructor
// for builder pattern to set value
@Builder
// for chain pattern to set value
@Accessors(chain = true)
class Student{
    private Integer id;
    private String studentName;
    private String major;
}
