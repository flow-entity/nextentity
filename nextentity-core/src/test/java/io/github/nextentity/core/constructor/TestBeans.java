package io.github.nextentity.core.constructor;

/**
 * Test bean types for {@link ObjectConstructorTest}.
 * Defined as top-level public static classes so that {@code DefaultSchema.of(type).getConstructor()}
 * can find the public no-arg constructor via reflection.
 */
public final class TestBeans {

    private TestBeans() {}

    public static class SimpleBean {
        private String name;
        private int age;

        public SimpleBean() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }

    public static class FullBean {
        private String name;
        private Integer count;
        private Boolean active;

        public FullBean() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
        public Boolean getActive() { return active; }
        public void setActive(Boolean active) { this.active = active; }
    }

    public static class SingleFieldBean {
        private String value;

        public SingleFieldBean() {}

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    public static class NoSetterBean {
        private String name;

        public NoSetterBean() {}

        public String getName() { return name; }
        // No setter - field will be accessed directly
    }

    public static class ThrowingSetterBean {
        private String name;

        public ThrowingSetterBean() {}

        public String getName() { return name; }
        public void setName(String name) {
            throw new RuntimeException("setter exploded");
        }
    }

    public static class ThrowingConstructorBean {
        private String name;

        public ThrowingConstructorBean() {
            throw new RuntimeException("ctor failed");
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
