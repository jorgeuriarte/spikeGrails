package spikegrails



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(CFace)
@Mock(CNose)
class CFaceTests {

    void testSaveFaceAndNoseOK() {
       new CFace(nose:new CNose()).save(failOnError:true,flush: true)

       assert CFace.count() == 1
       assert CNose.count() == 1 //hasOne == cascade save
    }

    void testSaveFaceAndNoseNOK() {
    	shouldFail {
       		new CNose(face:new CFace().save(failOnError:true)).save(failOnError:true, flush: true)
   		}
    }

    void testDeleteFaceCascadeNose() {
       	def face = new CFace(nose:new CNose()).save(failOnError:true, flush: true)

    	assert CFace.count() == 1
    	assert CNose.count() == 1 //hasOne == cascade save

    	face.delete(flush: true)

    	assert CFace.count() == 0
    	assert CNose.count() == 1 //hasOne == No cascade delete
    }
}
