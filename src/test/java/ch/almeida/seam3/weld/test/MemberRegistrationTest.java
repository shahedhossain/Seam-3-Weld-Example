package ch.almeida.seam3.weld.test;

import static org.junit.Assert.assertNotNull;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.almeida.seam3.weld.controller.MemberRegistration;
import ch.almeida.seam3.weld.data.MemberRepository;
import ch.almeida.seam3.weld.data.MemberRepositoryProducer;
import ch.almeida.seam3.weld.model.Member;

@RunWith(Arquillian.class)
public class MemberRegistrationTest {
	@Deployment
	public static Archive<?> createTestArchive() {
		return ShrinkWrap.create(WebArchive.class, "test.war")
				.addClasses(Member.class, MemberRegistration.class, MemberRepository.class, MemberRepositoryProducer.class)
				.addManifestResource("test-persistence.xml", "persistence.xml").addWebResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Inject
	MemberRegistration memberRegistration;

	@Inject
	Logger log;

	@Test
	public void testRegister() throws Exception {
		Member member = memberRegistration.getMember();
		member.setName("Jane Doe");
		member.setEmail("jane@mailinator.com");
		member.setPhoneNumber("2125551234");
		memberRegistration.save();
		assertNotNull(member.getId());
		log.info(member.getName() + " was persisted with id " + member.getId());
	}

	@Produces
	public Logger produceLog(InjectionPoint injectionPoint) {
		return Logger.getLogger(injectionPoint.getMember().getDeclaringClass());
	}
}
