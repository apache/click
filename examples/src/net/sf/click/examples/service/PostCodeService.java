package net.sf.click.examples.service;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import net.sf.click.examples.domain.PostCode;
import net.sf.click.extras.cayenne.CayenneTemplate;

/**
 * Provides a Postcode Service.
 *
 * @see PostCode
 *
 * @author Malcolm Edgar
 */
public class PostCodeService extends CayenneTemplate {

    public List getPostCodeLocations(String location) {
        SelectQuery query = new SelectQuery(PostCode.class);

        query.andQualifier(ExpressionFactory.likeIgnoreCaseExp(PostCode.LOCALITY_PROPERTY, location + "%"));

        query.addOrdering(PostCode.LOCALITY_PROPERTY, true);

        query.setFetchLimit(10);

        List list = performQuery(query);

        for (int i = 0; i < list.size(); i++) {
            PostCode postCode = (PostCode) list.get(i);
            String value = postCode.getLocality() + ", " + postCode.getState() + " " + postCode.getPostCode();
            list.set(i, value);
        }

        return list;
    }

}
