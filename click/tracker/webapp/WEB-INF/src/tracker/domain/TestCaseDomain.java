/*
 * Copyright 2005 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tracker.domain;

import java.util.Date;

import junit.framework.TestCase;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import tracker.util.SessionProvider;

/**
 * Provides domain package TestCase.
 *
 * @author Malcolm Edgar
 */
public class TestCaseDomain extends TestCase {

    public void testUsers() throws Exception {

        Session session = SessionProvider.getSession();
        
        Transaction tx = session.beginTransaction();
        
        User newUser = new User();
        newUser.setUsername("medgar");
        newUser.setPassword("password");
        newUser.setCreatedAt(new Date());
        newUser.setCreatedBy("medgar");
        newUser.setUpdatedAt(new Date());
        newUser.setUpdatedBy("medgar");
        
        session.save(newUser);
        
        UserRole userRole = new UserRole();
        userRole.setUser(newUser);
        userRole.setRole("user");
        
        session.save(userRole);
        
/*        
        User savedUser = (User) session.get(User.class, "medgar");
        
        assertEquals(newUser.toString(), savedUser.toString());
        
        Status status = (Status) session.get(Status.class, new Long(0));
        Severity severity = (Severity) session.get(Severity.class, new Long(0));
        Priority priority = (Priority) session.get(Priority.class, new Long(0));
        Category category = (Category) session.get(Category.class, new Long(0));
        Version version = (Version) session.get(Version.class, new Long(0));
        
        Issue newIssue = new Issue();
        
        newIssue.setStatus(status);
        newIssue.setSeverity(severity);
        newIssue.setPriority(priority);
        newIssue.setCategory(category);
        newIssue.setVersion(version);
        newIssue.setSummary("summary");
        newIssue.setDescription("description");
        newIssue.setCreatedBy(savedUser);
        newIssue.setCreatedAt(new Date());
        newIssue.setUpdatedBy(savedUser);
        newIssue.setUpdatedAt(new Date());
        
        session.save(newIssue);
        
        session.delete(newIssue);
        
        session.delete(savedUser);
*/       
        tx.commit();
        
        SessionProvider.closeSession();
    }

}
