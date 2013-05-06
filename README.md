# sql-writer

Uses types, JPA annotations and a fluent interface to automate SQL's boring 80%, without getting in your way.

sql-writer is inspired by [squel.js](http://hiddentao.github.io/squel/).

## Installation

Requires Java 6 and Maven.

sql-writer is in Maven Central. Add the following to your POM:

````xml
<dependency>
  <groupId>co.mewf.sqlwriter</groupId>
  <artifactId>sql-writer</artifactId>
  <version>0.1.0</version>
</dependency>
````

## Usage

It is recommendend to only generate the SQL once, either by storing it in constants or in instance variables in a class that is only instantiated once.

The query builders are accessible from the Queries class as static methods.

````java
import static co.mewf.sqlwriter.Queries.*;

select().from(User.class).sql(); // SELECT User.* FROM User
update(User.class).set("name", "birthDate").where().eq("id").sql(); // UPDATE User SET User.name = ?, User.birthDate = ? WHERE User.id = ?
insert(User.class).columns("name", "birthDate").sql(); // INSERT INTO User(name, birthDate) VALUES(?, ?)
delete(User.class).where().eq("id").sql(); // DELETE FROM User WHERE id = ?
````

Annotations such as @Table and @Column are taken into account.

````java
@Table(name = "usr_tbl")
class User{
  @Column(name = "usr_name")
  private String name;
}

select().from(User.class).columns("name").sql(); // SELECT usr_tbl.usr_name FROM usr_tbl
````

As in JPA, the @Id annotation is required. It determines whether field or property access is used: place it on a field for field access or on a method for method access.

### Joins

Joins are derived from the JPA annotations. Unidirectional and bidirectional @OneToOne, @OneToMany, @ManyToOne and @ManyToMany are supported. @JoinTable and @JoinColumn are also supported.

````java
class Address {
  @ManyToOne
  private User user;
}

select().from(User.class).from(Address.class).sql(); // SELECT User.*, Address.* FROM User INNER JOIN Address ON User.id = Address.user_id
select().from(Address.class).join(User.class).sql(); // SELECT Address.* FROM User INNER JOIN Address ON User.id = Address.user_id
select().from(Address.class).leftJoin(User.class).sql(); // SELECT Address.* FROM User LEFT JOIN Address ON User.id = Address.user_id
select().from(Address.class).rightJoin(User.class).sql(); // SELECT Address.* FROM User RIGHT JOIN Address ON User.id = Address.user_id
````

In a bidirectional one-to-one relationship, the foreign key column is assumed to be on the owning table, ie. the one without a value for mappedBy in the OneToOne annotation.

In a many-to-many relationship, the default join table is the name of the owning table and the name of the inverse table, separated by an underscore.

### Extra clauses

sql-writer tries to stay close to SQL syntax, but there is one significant departure: the context for WHERE and ORDER BY clauses is given by the preceding call to `join` or `from`.

````java
select().from(User.class).where().eq("id").from(Address.class).where().eq("street").sql(); // SELECT User.*, Address.* FROM User INNER JOIN Address ON User.id = Address.user_id WHERE User.id = ? AND Address.street = ?
````

### Shortcuts

When no columns are specified in an update or insert query, all columns are included. By default, certain columns are excluded: generated values, static or transient fields, `@Column(insertable = false)`, `@Column(updatable = false)`, etc.

## License

sql-writer is copyright Moandji Ezana and licensed under the MIT License.

## TODO

* Allow customisation of how column and table names are generated
* Add WhereBuilder methods that take a class. Join class to current context if not already joined.
* Support alternative SQL syntaxes
* Generate mappings for lightweight field-name safety
