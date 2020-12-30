package test.com.wwh.rpm.common.serialize;

public class StudentPojo {

    private String name;
    private Integer age;
    private Boolean gender;

    public StudentPojo() {
    }

    public StudentPojo(String name, Integer age, Boolean gender) {
        super();
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "StudentPojo [name=" + name + ", age=" + age + ", gender=" + gender + "]";
    }

}
