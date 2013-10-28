package spikegrails



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(AAuthor)
@Mock(ABook)
class AAuthorTests {
    void testSaveAuthorAndBookOK() {
		new AAuthor(name:"Stephen King")
		        .addToBooks(new ABook(title:"The Stand"))
		        .save(failOnError:true, flush: true)

    	assert AAuthor.count() == 1
    	assert ABook.count() == 1 // no belongsTo == cascade save
    }

    void testSaveAuthorAndBookNOK() {
    	shouldFail {
			new ABook(title:"The Stand")
			        .addToBooks(new AAuthor(title:"Stephen King"))
			        .save(failOnError:true, flush: true)
   		}
    }

    void testDeleteAuthorCascadeBook() {
		def author = new AAuthor(name:"Stephen King")
		        .addToBooks(new ABook(title:"The Stand"))
		        .save(failOnError:true, flush: true)

    	assert AAuthor.count() == 1
    	assert ABook.count() == 1 //no belongsTo == cascade save

    	author.delete(failOnError:true, flush: true)

    	assert AAuthor.count() == 0
    	assert ABook.count() == 1 //no belongsTo == No cascade delete
    }
}
