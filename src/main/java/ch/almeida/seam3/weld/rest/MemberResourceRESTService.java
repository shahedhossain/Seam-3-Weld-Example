package ch.almeida.seam3.weld.rest;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;
import org.jboss.seam.solder.logging.Category;

import ch.almeida.seam3.weld.data.MemberRepository;
import ch.almeida.seam3.weld.model.Member;

/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the members
 * table.
 */
@Path("/members")
@RequestScoped
public class MemberResourceRESTService {

	@Inject
	@Category("seam3-weld")
	private Logger log;

	@Inject
	@MemberRepository
	private EntityManager em;

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public List<Member> listAllMembers() {
		// Use @SupressWarnings to force IDE to ignore warnings about
		// "genericizing" the results of this query
		@SuppressWarnings("unchecked")
		// We recommend centralizing inline queries such as this one into
		// @NamedQuery annotations on the @Entity class
		// as described in the named query blueprint:
		// https://blueprints.dev.java.net/bpcatalog/ee5/persistence/namedquery.html
		final List<Member> results = em.createNamedQuery("findAllMembers")
				.getResultList();
		return results;
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/{id:[1-9][0-9]*}")
	public Member lookupMemberById(@PathParam("id") long id) {
		Member member = em.find(Member.class, id);
		log.debug(member);
		return member;
	}
}
