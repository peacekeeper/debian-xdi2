/* -----------------------------------------------------------------------------
 * Rule_data_iri.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.3
 * Produced : Thu Mar 14 16:34:39 CET 2013
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.parser.full.aparse;

import java.util.ArrayList;

final public class Rule_data_iri extends Rule
{
  private Rule_data_iri(String spelling, ArrayList<Rule> rules)
  {
    super(spelling, rules);
  }

  public Object accept(Visitor visitor)
  {
    return visitor.visit(this);
  }

  public static Rule_data_iri parse(ParserContext context)
  {
    context.push("data-iri");

    boolean parsed = true;
    int s0 = context.index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    parsed = false;
    if (!parsed)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = context.index;
        parsed = true;
        if (parsed)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = Terminal_StringValue.parse(context, "data:,");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          parsed = c1 == 1;
        }
        if (parsed)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = Rule_iri_char.parse(context);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          while (f1)
          {
            rule = Rule_iri_char.parse(context);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          parsed = c1 >= 1;
        }
        if (parsed)
          e0.addAll(e1);
        else
          context.index = s1;
      }
    }

    rule = null;
    if (parsed)
      rule = new Rule_data_iri(context.text.substring(s0, context.index), e0);
    else
      context.index = s0;

    context.pop("data-iri", parsed);

    return (Rule_data_iri)rule;
  }
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
