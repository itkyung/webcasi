package com.kbsmc.webcasi.common;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * JSON Serialize 해 주는 기능.
 * <p>
 * JSONSerializer is the main class for performing serialization of Java objects
 * to JSON.  JSONSerializer by default performs a shallow serialization.  While
 * this might seem strange there is a method to this madness.  Shallow serialization
 * allows the developer to control what is serialized out of the object graph.
 * This helps with performance, but more importantly makes good OO possible, fixes
 * the circular reference problem, and doesn't require boiler plate translation code.
 * You don't have to change your object model to make JSON work so it reduces your
 * work load, and keeps you
 * <a href="http://en.wikipedia.org/wiki/Don't_repeat_yourself">DRY</a>.
 * </p>
 *
 * <p>
 * Let's go through a simple example:
 * </p>
 *
 * <pre>
 *    JSONSerializer serializer = jsonFactory.create();
 *    return serializer.serialize( person );
 *
 * </pre>
 *
 * <p>
 * What this statement does is output the json from the instance of person.  So
 * the JSON we might see for this could look like:
 * </p>
 *
 * <pre>
 *    { "class": "com.mysite.Person",
 *      "firstname": "Charlie",
 *      "lastname": "Rose",
 *      "age", 23
 *      "birthplace": "Big Sky, Montanna"
 *    }
 *
 * </pre>
 * <p>
 * In this case it's look like it's pretty standard stuff.  But, let's say
 * Person had many hobbies (i.e. Person.hobbies is a java.util.List).  In
 * this case if we executed the code above we'd still get the same output.
 * This is a very important feature of flexjson, and that is any instance
 * variable that is a Collection, Map, or Object reference won't be serialized
 * by default.  This is what gives flexjson the shallow serialization.
 * </p>
 *
 * <p>
 * How would we include the <em>hobbies</em> field?  Using the {@link com.bizwave.cgntv.model.json.samsung.json.JSONSerializer#include}
 * method allows us to include these fields in the serialization process.  Here is
 * how we'd do that:
 * </p>
 *
 * <pre>
 *    return jsonFactory.create().include("hobbies").serialize( person );
 *
 * </pre>
 *
 * That would produce output like:
 *
 * <pre>
 *    { "class": "com.mysite.Person",
 *      "firstname": "Charlie",
 *      "lastname": "Rose",
 *      "age", 23
 *      "birthplace": "Big Sky, Montanna",
 *      "hobbies", [
 *          "poker",
 *          "snowboarding",
 *          "kite surfing",
 *          "bull riding"
 *      ]
 *    }
 *
 * </pre>
 *
 * <p>
 * If the <em>hobbies</em> field contained objects, say Hobby instances, then a
 * shallow copy of those objects would be performed.  Let's go further and say
 * <em>hobbies</em> had a List of all the people who enjoyed this hobby.
 * This would create a circular reference between Person and Hobby.  Since the
 * shallow copy is being performed on Hobby JSONSerialize won't serialize the people
 * field when serializing Hobby instances thus breaking the chain of circular references.
 * </p>
 *
 * <p>
 * But, for the sake of argument and illustration let's say we wanted to send the
 * <em>people</em> field in Hobby.  We can do the following:
 * </p>
 *
 * <pre>
 *    return jsonFactory.create().include("hobbies.people").serialize( person );
 *
 * </pre>
 *
 * <p>
 * JSONSerializer is smart enough to know that you want <em>hobbies</em> field included and
 * the <em>people</em> field inside hobbies' instances too.  The dot notation allows you
 * do traverse the object graph specifying instance fields.  But, remember a shallow copy
 * will stop the code from getting into an infinte loop.
 * </p>
 *
 * <p>
 * You can also use the exclude method to exclude fields that would be included.  Say
 * we have a User object.  It would be a serious security risk if we sent the password
 * over the network.  We can use the exclude method to prevent the password field from
 * being sent.
 * </p>
 *
 * <pre>
 *   return new JSONSerialize().exclude("password").serialize(user);
 *
 * </pre>
 *
 * <p>
 * JSONSerializer will also pay attention to any method or field annotated by
 * {@link com.samsung.common.annotation.JSON}.  You can include and exclude fields permenantly using the
 * annotation.  This is good like in the case of User.password which should never
 * ever be sent through JSON.  However, fields like <em>hobbies</em> or
 * <em>favoriteMovies</em> depends on the situation so it's best NOT to annotate
 * those fields, and use the {@link com.bizwave.cgntv.model.json.samsung.json.JSONSerializer#include} method.
 * </p>
 *
 * <p>
 * In a shallow copy only these types of instance fields will be sent:
 * <strong>String</strong>, <strong>Date</strong>, <strong>Number</strong>,
 * <strong>Boolean</strong>, <strong>Character</strong>, <strong>Enum</strong>,
 * <strong>Object</strong> and <strong>null</strong>.  Subclasses of Object will be serialized
 * except for Collection or Arrays.  Anything that would cause a N objects would not be sent.
 * All types will be excluded by default.  Fields marked static or transient are not serialized.
 * </p>
 * <p>
 * Includes and excludes can include wildcards.  Wildcards allow you to do things like exclude
 * all class attributes.  For example *.class would remove the class attribute that all objects
 * have when serializing.  A open ended wildcard like * would cause deep serialization to take
 * place.  Be careful with that one.  Although you can limit it's depth with an exclude like
 * *.foo.  The order of evaluation of includes and excludes is the order in which you called their
 * functions.  First call to those functions will cause those expressions to be evaluated first.
 * The first expression to match a path that action will be taken thus short circuiting all other
 * expressions defined later.
 * </p>
 * <p>
 * Transforers are a new addition that allow you to modify the values that are being serialized.
 * This allows you to create different output for certain conditions.  This is very important in
 * web applications.  Say you are saving your text to the DB that could contain &lt; and &gt;.  If
 * you plan to add that content to your HTML page you'll need to escape those characters.  Transformers
 * allow you to do this.  Flexjson ships with a simple HTML encoder {@link com.samsung.json.impl.HTMLEncoder}.
 * Transformers are specified in dot notation just like include and exclude methods, but it doesn't
 * support wildcards.
 * </p>
 * <p>
 * JSONSerializer is safe to use the serialize() methods from two seperate
 * threads.  It is NOT safe to use combination of {@link com.bizwave.cgntv.model.json.samsung.json.JSONSerializer#include(String[])}
 * {@link JSONSerializer#transform(Transformer, String[])}, or {@link com.bizwave.cgntv.model.json.samsung.json.JSONSerializer#exclude(String[])}
 * from multiple threads at the same time.  It is also NOT safe to use
 * {@link com.bizwave.cgntv.model.json.samsung.json.JSONSerializer#serialize(String, Object)} and include/exclude/transform from
 * multiple threads.  The reason for not making them more thread safe is to boost performance.
 * Typical use case won't call for two threads to modify the JSONSerializer at the same type it's
 * trying to serialize.
 * </p>
 * @author uchung
 * @title JSON serialize 해주는 기능
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class JSONSerializer {
  static final Log log = LogFactory.getLog(JSONSerializer.class);

  private List<PathExpression> pathExpressions = new ArrayList<PathExpression>();
  private Map<Path, Transformer> transformations = new HashMap<Path,Transformer>();
  private List<Transformer> allTransformers = new ArrayList<Transformer>();
  private Map<String, Map<String,Method>> classAccessors = new HashMap<String, Map<String,Method>>();
  private boolean includeAllMap=false;

  /**
   * Create a serializer instance.  It's unconfigured in terms of fields
   * it should include or exclude. includeAllMap은 default false
   */
  public JSONSerializer() {
  }

  /**
   * JSONSerializer 를 생성함
   * @param includeAllMap true이면 Map의 모든 내용을 다 무조건 보냄. false면 Map도 object와
   *   같은 조건으로 check함 (즉 include/exclude체크하고, collection/Map default로 안보냄)
   */
  public JSONSerializer(boolean includeAllMap) {
    this.includeAllMap = includeAllMap;
  }

 

  /**
   * serialize 함
   */
  public String serialize(String rootName, Object target) {
    StringWriter writer = new StringWriter();
    try {
      this.serialize(rootName, target, writer);
    } catch (IOException e) {
      // Can never happen with StringWriter
    }
    return writer.toString();
  }

  /**
   * serialize 함
   */
  public String serialize(Object target) {
    StringWriter writer = new StringWriter();
    try {
      this.serialize(target, writer);
    } catch (IOException e) {
      // Can never happen with StringWriter
    }
    return writer.toString();
  }  

  /**
   * This performs a shallow serialization of target instance.  It wraps
   * the resulting JSON in a javascript object that contains a single field
   * named rootName.  This is great to use in conjunction with other libraries
   * like EXTJS whose data models require them to be wrapped in a JSON object.
   * 
   * @param rootName the name of the field to assign the resulting JSON.
   * @param target the instance to serialize to JSON.
   * @return the JSON object with one field named rootName and the value being the JSON of target.
   * @throws IOException 
   */
  public void serialize( String rootName, Object target, Writer writer ) throws IOException {
    new ShallowVisitor(writer).visit( rootName, target );
  }




  /**
   * This performs a shallow serialization of the target instance.
   *
   * @param target the instance to serialize to JSON
   * @return the JSON representing the target instance.
   * @throws IOException 
   */
  public void serialize( Object target, Writer writer ) throws IOException {
    new ShallowVisitor(writer).visit( target );
  }

  /**
   * This performs a deep serialization of the target instance.  It will include
   * all collections, maps, and arrays by default so includes are ignored except
   * if you want to include something being excluded by an annotation.  Excludes
   * are honored.  However, cycles in the target's graph are NOT followed.  This
   * means some members won't be included in the JSON if they would create a cycle.
   * Rather than throwing an exception the cycle creating members are simply not
   * followed.
   *
   * @param target the instance to serialize to JSON.
   * @return the JSON representing the target instance deep serialization.
   * @throws IOException 
   */
  public void deepSerialize( Object target, Writer writer ) throws IOException {
    new DeepVisitor(writer).visit( target );
  }

  /**
   * This performs a deep serialization of target instance.  It wraps
   * the resulting JSON in a javascript object that contains a single field
   * named rootName.  This is great to use in conjunction with other libraries
   * like EXTJS whose data models require them to be wrapped in a JSON object.
   * See {@link com.bizwave.cgntv.model.json.samsung.json.JSONSerializer#deepSerialize(Object)} for more
   * in depth explaination.
   *
   * @param rootName the name of the field to assign the resulting JSON.
   * @param target the instance to serialize to JSON.
   * @return the JSON object with one field named rootName and the value being the JSON of target.
   * @throws IOException 
   */
  public void deepSerialize( String rootName, Object target, Writer writer ) throws IOException {
    new DeepVisitor(writer).visit( rootName, target );
  }

  /**
   * This takes in a dot expression representing fields
   * to exclude when serialize method is called.  You
   * can hand it one or more fields.  Example are: "password",
   * "bankaccounts.number", "people.socialsecurity", or
   * "people.medicalHistory".  In exclude method dot notations
   * will only exclude the final field (i.e. rightmost field).
   * All the fields to the left of the last field will be included.
   * In order to exclude the medicalHistory field we have to
   * include the people field since people would've been excluded
   * anyway since it's a Collection of Person objects.  The order of
   * evaluation is the order in which you call the exclude method.
   * The first call to exclude will be evaluated before other calls to
   * include or exclude.  The field expressions are evaluated in order
   * you pass to this method.
   *
   * @param fields one or more field expressions to exclude.
   * @return this instance for method chaining.
   */
  public JSONSerializer exclude( String... fields ) {
    for( String field : fields ) {
      addExclude( field );
    }
    return this;
  }

  /**
   * This takes in a dot expression representing fields to
   * include when serialize method is called.  You can hand
   * it one or more fields.  Examples are: "hobbies",
   * "hobbies.people", "people.emails", or "character.inventory".
   * When using dot notation each field between the dots will
   * be included in the serialization process.  The order of
   * evaluation is the order in which you call the include method.
   * The first call to include will be evaluated before other calls to
   * include or exclude.  The field expressions are evaluated in order
   * you pass to this method.
   *
   * @param fields one or more field expressions to include.
   * @return this instance for method chaining.
   */
  public JSONSerializer include( String... fields ) {
    for( String field : fields ) {
      pathExpressions.add( new PathExpression( field, true ) );
    }
    return this;
  }

  /**
   * This adds a Transformer used to manipulate the value of all the fields you give it.
   * Fields can be in dot notation just like {@link JSONSerializer#include} and
   * {@link JSONSerializer#exclude } methods.  However, transform doesn't support wildcards.
   * Specifying more than one field allows you to add a single instance to multiple fields.
   * It's there for handiness. :-) 
   * @param transformer the instance used to transform values
   * @param fields the paths to the fields you want to transform.  They can be in dot notation.
   * @return Hit you back with the JSONSerializer for method chain goodness.
   */
  public JSONSerializer transform( Transformer transformer, String... fields ) {
    if (fields == null || fields.length == 0) {
      allTransformers.add(transformer);
    } else {
      for( String field : fields ) {
        if( field.length() == 0 ) {
          transformations.put( new Path(), transformer );
        } else {
          transformations.put( new Path( StringUtils.splitPreserveAllTokens(field, ',') ), transformer );
        }
      }
    }
    return this;
  }

  /**
   * Return the fields included in serialization.  These fields will be in dot notation.
   *
   * @return A List of dot notation fields included in serialization.
   */
  public List<PathExpression> getIncludes() {
    List<PathExpression> expressions = new ArrayList<PathExpression>();
    for( PathExpression expression : pathExpressions ) {
      if( expression.isIncluded() ) {
        expressions.add( expression );
      }
    }
    return expressions;
  }

  /**
   * Return the fields excluded from serialization.  These fields will be in dot notation.
   *
   * @return A List of dot notation fields excluded from serialization.
   */
  public List<PathExpression> getExcludes() {
    List<PathExpression> excludes = new ArrayList<PathExpression>();
    for( PathExpression expression : pathExpressions ) {
      if( !expression.isIncluded() ) {
        excludes.add( expression );
      }
    }
    return excludes;
  }

  /**
   * Sets the fields included in serialization.  These fields must be in dot notation.
   * This is just here so that JSONSerializer can be treated like a bean so it will
   * integrate with Spring or other frameworks.  <strong>This is not ment to be used
   * in code use include method for that.</strong>
   * @param fields the list of fields to be included for serialization.  The fields arg should be a
   * list of strings in dot notation.
   */
  public void setIncludes( List fields ) {
    for( Object field : fields ) {
      pathExpressions.add( new PathExpression( field.toString(), true ) );
    }
  }

  /**
   * Sets the fields excluded in serialization.  These fields must be in dot notation.
   * This is just here so that JSONSerializer can be treated like a bean so it will
   * integrate with Spring or other frameworks.  <strong>This is not ment to be used
   * in code use exclude method for that.</strong>
   * @param fields the list of fields to be excluded for serialization.  The fields arg should be a 
   * list of strings in dot notation.
   */
  public void setExcludes( List fields ) {
    for( Object field : fields ) {
      addExclude( field );
    }
  }

  private void addExclude(Object field) {
    String name = field.toString();
    int index = name.lastIndexOf('.');
    if( index > 0 ) {
      PathExpression expression = new PathExpression( name.substring( 0, index ), true );
      if( !expression.isWildcard() ) {
        pathExpressions.add( expression );
      }
    }
    pathExpressions.add( new PathExpression( name, false ) );
  }

  /**
   * This will do a serialize the target and pretty print the output so it's easier to read.
   *
   * @param target of the serialization.
   * @return the serialized representation of the target in pretty print form.
   * @throws IOException 
   */
  public void prettyPrint( Object target, Writer writer ) throws IOException {
    new ShallowVisitor(writer, true ).visit( target );
  }

  /**
   * This will do a serialize with root name and pretty print the output so it's easier to read.
   *
   * @param rootName the name of the field to assign the resulting JSON.
   * @param target of the serialization.
   * @return the serialized representation of the target in pretty print form.
   * @throws IOException 
   */
  public void prettyPrint( String rootName, Object target, Writer writer ) throws IOException {
    new ShallowVisitor( writer, true ).visit( rootName, target );
  }

  int isAllowedShallowType(Class propType) {  
    if(propType == null) return NOT_INCLUDE;
    return (propType.equals(Class.class) || propType.isArray() || Iterable.class.isAssignableFrom(propType) || Map.class.isAssignableFrom(propType))?NOT_INCLUDE:INCLUDE_ID_ONLY;
  }
  
  /**
   * Path 관련 expression
   * @author uchung
   *
   */
  class PathExpression {
    String[] expression;
    boolean wildcard = false;
    boolean included = true;
    /**
     * PathExpression 객체를 생성함
     */
    public PathExpression( String expr, boolean anInclude ) {
      expression = StringUtils.splitPreserveAllTokens(expr, '.');
      wildcard = expr.indexOf('*') >= 0;
      included = anInclude;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("[");
      for( int i = 0; i < expression.length; i++ ) {
        builder.append( expression[i] );
        if( i < expression.length - 1 ) {
          builder.append(",");
        }
      }
      builder.append("]");
      return builder.toString();
    }
    /**
     * path 와 현 PathExpression 이 match 가 되는지 여부를 구함
     */
    public boolean matches( Path path ) {
      int exprCurrentIndex = 0;
      int pathCurrentIndex = 0;
      while( pathCurrentIndex < path.length() ) {
        String current = path.getPath().get( pathCurrentIndex );
        if( exprCurrentIndex < expression.length && expression[exprCurrentIndex].equals("*") ) {
          exprCurrentIndex++;
        } else if( exprCurrentIndex < expression.length && expression[exprCurrentIndex].equals( current ) ) {
          pathCurrentIndex++;
          exprCurrentIndex++;
        } else if( exprCurrentIndex - 1 >= 0 && expression[exprCurrentIndex-1].equals("*") ) {
          pathCurrentIndex++;
        } else {
          return false;
        }
      }
      if( exprCurrentIndex > 0 && expression[exprCurrentIndex-1].equals("*") ) {
        return pathCurrentIndex >= path.length() && exprCurrentIndex >= expression.length;
      }
      return pathCurrentIndex >= path.length() && path.length() > 0;
    }
    /**
     * 이 path expression 에 wildcard 가 있는지
     */
    public boolean isWildcard() {
      return wildcard;
    }
    /**
     * include 되는 건지여부
     */
    public boolean isIncluded() {
      return included;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      PathExpression that = (PathExpression) o;

      if (!Arrays.equals(expression, that.expression)) return false;

      return true;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
      return (expression != null ? Arrays.hashCode(expression) : 0);
    }
  }
  
  /**
   * Path 정보를 담고 있음
   * @author uchung
   *
   */
  class Path {
    LinkedList<String> path = new LinkedList<String>();

    public Path() {
    }

    /**
     * Path 객체를 생성함
     */
    public Path( String... fields ) {
      for (String field : fields) {
        path.add(field);
      }
    }
    /**
     * 새로운 path component 를 하나 추가 함
     */
    public Path enqueue( String field ) {
      path.add( field );
      return this;
    }

    /**
     * path component 하나를 뺌
     */
    public String pop() {
      return path.removeLast();
    }
    /**
     * 현 path 를 string list 로 구함
     * @return
     */
    public List<String> getPath() {
      return path;
    }

    /**
     * path 수
     */
    public int length() {
      return path.size();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
      StringBuilder builder = new StringBuilder ( "[ " );
      boolean afterFirst = false;
      for( String current : path ) {
        if( afterFirst ) {
          builder.append( "." );
        }
        builder.append( current );
        afterFirst = true;
      }
      builder.append( " ]" );
      return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Path path1 = (Path) o;

      if (!path.equals(path1.path)) return false;

      return true;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
      return path.hashCode();
    }
  }  

  /**
   * Object 를 visit 하는 class
   * @author uchung
   *
   */
  abstract class ObjectVisitor implements IObjectVisitor {
    /**
     * 
     */
    private final JSONSerializer serializer;
    private Writer writer; 
    protected boolean prettyPrint = false;
    private int amount = 0;
    private boolean insideArray = false;
    private Path path;

    protected ObjectVisitor(JSONSerializer serializer, Writer writer) {
      this.serializer = serializer;
      path = new Path();
      this.writer = writer;
    }

    protected ObjectVisitor(JSONSerializer serializer, Writer writer, boolean prettyPrint) {
      this(serializer, writer);
      this.prettyPrint = prettyPrint;
    }

    void visit( Object target ) throws IOException {
      json( target, true, null, false );
    }

    void visit( String rootName, Object target ) throws IOException {
      beginObject();
      if (json( target, true, null, false )!=false) {
        addAttribute(rootName);
        addTimezone(rootName, target);
      }
      endObject();
    }

    private void addTimezone(String rootName, Object target) throws IOException {
      if (target instanceof Calendar) {
        writer.append(',').append(rootName).append("_tz:'");
        int rawOffset = ((Calendar)target).getTimeZone().getRawOffset();
        writer.append((rawOffset >= 0)?'+':'-');
        if (rawOffset < 0) rawOffset = -1;
        int min = rawOffset / (1000 * 60) % 60;
        int hour = rawOffset / (1000 * 3600);
        if (hour < 10) writer.append('0');
        writer.append(Integer.toString(hour)).append(':');
        if (min < 10) writer.append('0');
        writer.append(Integer.toString(min)).append("'");
      }
    }

    private boolean json(Object object, boolean firstField, Object key, boolean forceInclude) throws IOException {
      return _json(object, firstField, key, true, null, false, forceInclude);
    }

    /**
     * 
     * @param object
     * @param transform
     * @param extra
     * @return object를 기타 이유로 add안했으면 false, 했으면 true
     * @throws IOException 
     */
    private boolean _json(Object object, boolean firstField, Object key,
        boolean transform, Map<String,? extends Object> extra, boolean dontCheckSerializable, boolean forceInclude) throws IOException {
      if (object == null) {
        addComma(firstField);
        addAttribute(key);
        _add("null");
      } else if (object instanceof Class) {
        return string(((Class)object).getName(), firstField, key, transform);
      } else if (object instanceof Boolean) {
        addComma(firstField);
        addAttribute(key);
        bool( ((Boolean) object) );
      } else if (object instanceof Number) {
        addComma(firstField);
        addAttribute(key);
        _add( doTransform( object, transform ) );
      } else if (object instanceof String) {
        return string(object, firstField, key, transform);
      } else if (object instanceof Currency) {
        return string(((Currency) object).getCurrencyCode(), firstField, key, transform);
      } else if (object instanceof Character) {
        return string(object, firstField, key, transform);
      } else if (object instanceof Map) {
        addComma(firstField);
        addAttribute(key);
        map( (Map)object, forceInclude);
      } else if (object.getClass().isArray()) {
        addComma(firstField);
        addAttribute(key);
        array( object );
      } else if (object instanceof Collection || (object instanceof Iterable)) { //&& object instanceof IsList
        addComma(firstField);
        addAttribute(key);
        array(((Iterable) object).iterator() );
      } else if( object instanceof Date) {
        date( (Date)object, firstField, key, transform);
      } else if (object instanceof Calendar) {
        calendar( (Calendar)object, firstField, key, transform);
      } else if( object instanceof Enum )
        return string( ((Enum)object).name(), firstField, key, transform );
      else {
        if( transform) {
          Object transformed = object;
          Map<String,Object> extra1 = new HashMap<String, Object>(2);
          if (this.serializer.transformations.containsKey( path ) ) {
            this.serializer.transformations.get( path ).transform(transformed, extra1, path.getPath());
          }
          for (Transformer t : this.serializer.allTransformers) {
            transformed = t.transform(transformed, extra1, path.getPath());
            if (transformed == null) return false;
          }

          // JSONserializer는 visits field를 통해서 circular reference체크를 함.
          // visits에는 object() method에서만 하는데, transform을 하면 때론
          // Object -> Map 으로 transform을 함으로 visits에 추가 되지 않은 경우가
          // 있어서 infinite loop이 생김.
          if(transformed != object && transformed instanceof Map && !(object instanceof Map)) {
            if (!visits.contains( object ) ) {
              visits = new ChainedSet( visits );
              visits.add( object );

              if (!_json(transformed, firstField, key, false, extra1.size()==0?null:extra1, dontCheckSerializable, false)) return false;

              visits = (ChainedSet) visits.getParent();
            }
          } else {
            return _json(transformed, firstField, key, false, extra1.size()==0?null:extra1, dontCheckSerializable, false);
          }
        } else {
          addComma(firstField);
          addAttribute(key);        
          bean( object, extra, dontCheckSerializable);
        }
      }
      return true;
    }

    private void map(Map map, boolean forceInclude) throws IOException {
      beginObject();

      boolean firstField = true;
      Set<Entry<Object,Object>> entrySet = map.entrySet();
      for (Entry<Object,Object> entry : entrySet) {
        Object key = entry.getKey();
        Object value = entry.getValue();
        String strKey = key.toString();

        if (forceInclude || this.serializer.includeAllMap) {
          if (this.path.path.size() == 0 || isIncluded(strKey, value==null?null:value.getClass()) != NOT_INCLUDE) {
            path.enqueue(strKey);
            boolean added = add( key, map.get(key), firstField, false );
            if (firstField && added) firstField = false;
            path.pop();
          }
        }
      }

      endObject();
    }

    private int isIncluded(String key, Class valueType) {
      path.enqueue(key);
      PathExpression matches = matches(key, this.serializer.pathExpressions);
      path.pop();
      if (matches != null) return matches.isIncluded() ? INCLUDE : NOT_INCLUDE;
      return this.serializer.isAllowedShallowType(valueType);
    }

    private void array(Iterator it) throws IOException {
      beginArray();
      boolean isFirst = true;
      while (it.hasNext()) {
        if( prettyPrint ) {
          addNewline();
        }
        boolean added = addArrayElement( it.next(), isFirst );
        if (added && isFirst) {
          isFirst = false;
        }
      }
      endArray();
    }

    private void array(Object object) throws IOException {
      beginArray();
      int length = Array.getLength(object);
      boolean isFirst = true;
      for (int i = 0; i < length; ++i) {
        if( prettyPrint ) {
          addNewline();
        }
        boolean added = addArrayElement( Array.get(object, i), isFirst );
        if (added && isFirst) {
          isFirst = false;
        }
      }
      endArray();
    }

    private boolean addArrayElement(Object object, boolean isFirst) throws IOException {
      return json( object, isFirst, null, false );
    }

    private void bool(Boolean b) throws IOException {
      _add( b ? "true" : "false" );
    }



    private void string(String str) throws IOException {
      _add(JSONUtils.toJSONString(str));
    }

    private boolean string(Object obj, boolean firstField, Object key, boolean transform) throws IOException {
      Object transformResult = doTransform( obj,transform );
      if (transformResult == null) return false;
      addComma(firstField);
      addAttribute(key);
      string(transformResult.toString());
      return true;
    }

    private void date(Date date, boolean addComma, Object key, boolean transform) throws IOException {
      if( this.serializer.allTransformers.size() > 0 || this.serializer.transformations.containsKey( path ) ) {
        Object obj = doTransform(date, true);
        if (obj instanceof Date) {
          addComma(addComma);
          addAttribute(key);
          date(date);
        } else {
          _json(obj, addComma, key, false, null, true, false);
        }
      } else {
        addComma(addComma);
        addAttribute(key);
        date(date);
      }
    }
    private void date(Date date) throws IOException {
      this._add(Long.toString((date).getTime()));
    }
    private void calendar(Calendar cal) throws IOException {
      add('"');
      this._add(Long.toString(cal.getTimeInMillis()));
      add('"');
    }
    private void calendar(Calendar cal, boolean firstField, Object key, boolean transform) throws IOException {
      if(transform && (this.serializer.allTransformers.size() > 0 ||this.serializer.transformations.containsKey( path ))) {
        Object obj = doTransform(cal, true);
        if (obj instanceof Calendar) {
          addComma(firstField);
          addAttribute(key);
          calendar(cal);
        } else {
          _json(obj, firstField, key, false, null, true, false);
        }
      } else {
        addComma(firstField);
        addAttribute(key);      
        calendar(cal);
      }
    }
    private ChainedSet visits = new ChainedSet( Collections.EMPTY_SET );

    private Map<String,Method> findAccessors(Class<?> cls) {
      String className = cls.getName();
      Map<String,Method> accessors = serializer.classAccessors.get(className);
      if (accessors == null) {
        accessors = new HashMap<String,Method>();
        serializer.classAccessors.put(className, accessors);
      }
      
      
      if (Object.class.equals(cls.getSuperclass()) && className.indexOf("$$") != -1) {
        for (Class<?> intf : cls.getInterfaces()) {
          String intfName = intf.getName();
          if (intfName.equals("org.hibernate.proxy.HibernateProxy") ||
              intfName.equals("javassist.util.proxy.ProxyObject") ||
              intfName.startsWith("org.springframework.aop.")) {
            continue;
          }
          IntrospectionUtils.addAccessors(accessors, intf.getMethods());
        }
      } else {
        IntrospectionUtils.addAccessors(accessors, cls.getMethods());
      }
      return accessors;
    }


    protected void bean(Object object, Map<String,? extends Object> extra, boolean dontCheckJSONSerializable) throws IOException {
      if( !visits.contains( object ) ) {
        visits = new ChainedSet( visits );
        visits.add( object );

        if (!dontCheckJSONSerializable && object instanceof IJSONSerializable) {
          ((IJSONSerializable) object).serialize(this);
        } else {
          beginObject();
          try {
            boolean hasAttribs = object instanceof IExtraAttributes;
            boolean hasAttribs2 = object instanceof IExtraAttributes2;
            boolean firstField = true;
            Map<String, Method> accessors = findAccessors(IntrospectionUtils.findBeanClass( object ));
            for (Entry<String, Method> entry : accessors.entrySet()) {
              String propName = entry.getKey();
              if (extra == null || !extra.containsKey(propName)) {
                if (hasAttribs && "attributes".equals(propName)) continue;
                if (hasAttribs2 && ("names".equals(propName)||"values".equals(propName))) continue;
                firstField = processProperty(object, hasAttribs, 
                    firstField, propName, entry.getValue());
              }
            }            
            if (hasAttribs) {
              Map<String, ? extends Object> a = ((IExtraAttributes)object).getAttributes();
              if (a != null && a.size() > 0) {
                for (Entry<String,? extends Object> entry : a.entrySet()) {
                  if (extra == null || !extra.containsKey(entry.getKey())) {
                    boolean added = add(entry.getKey(), entry.getValue(), firstField, false);
                    if (added && firstField) {
                      firstField = false;
                    }
                  }
                }
              }
            }
            if (hasAttribs2) {
              IExtraAttributes2 a2 = (IExtraAttributes2)object;
              String []names = a2.getNames();
              Object []values = a2.getValues();
              if (names != null && values != null) {
                int len = names.length<values.length?names.length:values.length;
                for (int i = 0; i < len; i++) {
                  String n = names[i];
                  Object v = values[i];
                  if (extra == null || !extra.containsKey(n)) {
                    boolean added = add(n, v, firstField, false);
                    if (added && firstField) {
                      firstField = false;
                    }
                  }
                }
              }
            }
            if (extra != null && extra.size() > 0) {
              for (Entry<String,? extends Object> entry : extra.entrySet()) {
                Object value = entry.getValue();
                if (value == null) continue;
                boolean added = add(entry.getKey(), value, firstField, false);
                if (added && firstField) {
                  firstField = false;
                }
              }
            }
          } catch( JSONException e ) {
            throw e;
          } catch( Exception e ) {
            throw new JSONException( "Error trying to serialize path: " + path.toString(), e );
          }
          endObject();
        }
        visits = (ChainedSet) visits.getParent();
      }
    }

    private boolean processProperty(Object object, boolean hasAttribs,
        boolean firstField, String name, Method accessor)
    throws IllegalAccessException, InvocationTargetException, IOException {
      if (!(hasAttribs && "attributes".equals(name))) { 
        path.enqueue( name );
        if (accessor != null) {
          int includedType = isIncluded( accessor );
          if (includedType != NOT_INCLUDE) {
            try {
              Object value = accessor.invoke(object, (Object[]) null);
              if( !visits.contains( value ) ) {
                Method idAccessor = null;
                if (includedType == INCLUDE_ID_ONLY && value != null && 
                    !value.getClass().getName().startsWith("java") &&
                    null != (idAccessor = findAccessors(IntrospectionUtils.findBeanClass(value)).get("id"))) {
                  Object id = idAccessor.invoke(value);
                  if (!firstField) {
                    writer.append(',');
                  }
                  writer.append('"').append(name).append("\":{");
                  json(id, true, "id", false);                 

                  writer.append("}");
                  firstField = false;
                } else {
                  boolean added = add(name, value, firstField, true);
                  if (added && firstField) {
                    firstField = false;
                  }
                }
              }
            } catch (Exception ex) {
              // Hibernate 가 Proxy 를 만들어 load  (inheritance 사용하는 경우)
              // 실제  select 한 instance 의 실제 객체보다 추가 interface 를 가지고 있는
              // 경우가 있는데, 여기서는 실제 instance 가 뭔지 모르기 때문에
              // proxy.getInterfaces() 를 통해서 밖에 할 수 없음으로 exception
              // 이 날 수 밖에 없음. 추후에는  첫번 째 이후에는 해당 interface 를
              // invalidIntf 에 저장해 놓고 이 interface 의 method 들은
              // 다시 사용 안함.
            }
          }
        }
        path.pop();
      }
      return firstField;
    }

    private Object doTransform(Object value, boolean transform) {
      if( transform && this.serializer.transformations.containsKey( path ) ) {
        value = this.serializer.transformations.get( path ).transform( value, null, path.getPath());
      }
      for (Transformer t : this.serializer.allTransformers) {
        value = t.transform(value, null, path.getPath());
      }
      return value;
    }


    /**
     * accessor 가 include 되었는지 여부
     */
    public abstract int isIncluded( Method accessor );
    /**
     * {@inheritDoc}
     */
    public abstract boolean isIncluded( String fieldName );

    protected boolean isValidField(Field field) {
      return !Modifier.isStatic( field.getModifiers() ) && Modifier.isPublic( field.getModifiers() ) && !Modifier.isTransient( field.getModifiers() );
    }

    private final void addComma(boolean firstField) throws IOException {
      if ( !firstField ) {
        add(',');
      }
    }

    protected void beginObject() throws IOException {
      if( prettyPrint ) {
        if( insideArray ) {
          indent( amount );
        }
        amount += 4;
      }
      add( '{' );
    }

    protected void endObject() throws IOException {
      if( prettyPrint ) {
        addNewline();
        amount -= 4;
        indent( amount );
      }
      add( '}' );
    }

    private void beginArray() throws IOException {
      if( prettyPrint ) {
        amount += 4;
        insideArray = true;
      }
      add('[');
    }

    private void endArray() throws IOException {
      if( prettyPrint ) {
        addNewline();
        amount -= 4;
        insideArray = false;
        indent( amount );
      }
      add(']');
    }

    protected void add( char c ) throws IOException {
      writer.append( c );
    }

    private void indent(int amount) throws IOException {
      for( int i = 0; i < amount; i++ ) {
        writer.append( " " );
      }
    }

    private void addNewline() throws IOException {
      writer.append('\n');
    }

    private final void _add( Object value ) throws IOException {
      writer.append( value==null?"null":value.toString() );
    }
    /**
     * {@inheritDoc}
     */
    public final IObjectVisitor raw( String str ) throws IOException {
      writer.append(str);
      return this;
    }

    private final boolean add(Object key, Object value, boolean firstField, boolean forceInclude) throws IOException {
      if (!"class".equals(key)) {
        boolean added = json( value, firstField, key, forceInclude );
        if (added) {
          addTimezone(key.toString(), value);
        }
        return added;
      }
      return false;
    }

    private void addAttribute(Object key) throws IOException {
      if (key == null) return;
      if( prettyPrint ) {
        addNewline();
        indent( amount );
      }
      string(key.toString());
      add(':');
      if( prettyPrint ) {
        add(' ');
      }
    }

    /**
     * key 와 expression 에 matching 되는 PathExpression 객체를 구함. match 가 없으면 null
     */
    public PathExpression matches(String key, List<PathExpression> expressions) {
      for( PathExpression expr : expressions ) {
        if( expr.matches( path ) ) {
          return expr;
        }
      }
      return null;
    }

    /**
     * {@inheritDoc}
     */
    public IObjectVisitor add(Object obj, boolean forceInclude) throws IOException {
      _json(obj, true, null, true, null, true, forceInclude);
      return this;
    }
    /**
     * {@inheritDoc}
     */
    public IObjectVisitor add(int v) throws IOException {
      _add(Integer.toString(v));
      return this;
    }
    /**
     * {@inheritDoc}
     */
    public IObjectVisitor add(boolean b) throws IOException {
      _add(b ? "true" : "false");
      return this;
    }    
  }
  private class ShallowVisitor extends ObjectVisitor {
    /**
     * ShallowVisitor 객체를 생성함
     */
    public ShallowVisitor(Writer writer) {
      super(JSONSerializer.this, writer);
    }

    public ShallowVisitor(Writer writer, boolean prettyPrint) {
      super(JSONSerializer.this, writer, prettyPrint);
    }

    public int isIncluded( Method accessor ) {
        PathExpression expression = matches( accessor.getName(), pathExpressions);
        if( expression != null ) {
          return expression.isIncluded() ? INCLUDE : NOT_INCLUDE;
        }

        Class propType = accessor.getReturnType();
        return isAllowedShallowType(propType);
      }

    @Override
    public boolean isIncluded( String fieldName) {
      PathExpression expression = matches( fieldName, pathExpressions);
      if( expression != null ) {
        return expression.isIncluded();
      }
      return true;
    }
  }



  private class DeepVisitor extends ObjectVisitor {

    public DeepVisitor(Writer writer) {
      super(JSONSerializer.this, writer);
    }

    public DeepVisitor(Writer writer, boolean prettyPrint) {
      super(JSONSerializer.this, writer, prettyPrint);
    }

    public int isIncluded( Method accessor ) {
      PathExpression expression = matches( accessor.getName(), pathExpressions);
      if( expression != null ) {
        return expression.isIncluded() ? INCLUDE : NOT_INCLUDE;
      }


      return INCLUDE_ID_ONLY;
    }

    @Override
    public boolean isIncluded( String fieldName) {
      PathExpression expression = matches( fieldName, pathExpressions);
      if( expression != null ) {
        return expression.isIncluded();
      }
      return true;
    }    
  }


  /**
   * Chained 된 set (두개 set 를 하나로)
   * @author uchung
   *
   */
  class ChainedSet implements Set {
    Set parent;
    Set child;

    /**
     * ChainedSet 객체를 생성함
     */
    public ChainedSet(Set parent) {
      this.parent = parent;
      this.child = new HashSet();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
      return this.child.size() + parent.size();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
      return this.child.isEmpty() && parent.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Object o) {
      return child.contains(o) || parent.contains(o);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator iterator() {
      return new ChainedIterator( child, parent );
    }

    /**
     * {@inheritDoc}
     */
    public Object[] toArray() {
      Object[] carr = child.toArray();
      Object[] parr = parent.toArray();
      Object[] combined = new Object[ carr.length + parr.length ];
      System.arraycopy( carr, 0, combined, 0, carr.length );
      System.arraycopy( parr, 0, combined, carr.length, parr.length );
      return combined;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] toArray(Object[] a) {
      throw new IllegalStateException( "Not implemeneted" );
    }

    /**
     * {@inheritDoc}
     */
    public boolean add(Object o) {
      return child.add( o );
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove(Object o) {
      return child.remove( o );
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsAll(Collection c) {
      return child.containsAll(c) || parent.containsAll(c); 
    }

    /**
     * {@inheritDoc}
     */
    public boolean addAll(Collection c) {
      return child.addAll( c );
    }

    /**
     * {@inheritDoc}
     */
    public boolean retainAll(Collection c) {
      return child.retainAll( c );
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeAll(Collection c) {
      return child.removeAll( c );
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
      child.clear();
    }

    /**
     * parent set 를 구함
     */
    public Set getParent() {
      return parent;
    }
  }
  
  /**
   * Chained 된 (즉 여러 iterator 를 연속으로 iterate 하는 기능) iterator
   * @author uchung
   *
   */
  class ChainedIterator implements Iterator {

    Iterator[] iterators;
    int current = 0;

    /**
     * ChainedIterator 객체를 생성함
     */
    public ChainedIterator(Set... sets) {
      iterators = new Iterator[sets.length];
      for( int i = 0; i < sets.length; i++ ) {
        iterators[i] = sets[i].iterator();
      }
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
      if( iterators[current].hasNext() ) {
        return true;
      }
      current++;
      return current < iterators.length && iterators[current].hasNext();
    }

    /**
     * {@inheritDoc}
     */
    public Object next() {
      return iterators[current].next();
    }

    /**
     * {@inheritDoc}
     */
    public void remove() {
      iterators[current].remove();
    }
  }

  private static final int NOT_INCLUDE = 0;
  private static final int INCLUDE = 1;
  private static final int INCLUDE_ID_ONLY = 2;

}
