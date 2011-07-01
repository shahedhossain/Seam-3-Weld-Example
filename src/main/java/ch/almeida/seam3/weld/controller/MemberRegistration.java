package ch.almeida.seam3.weld.controller;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.logging.Logger;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.solder.logging.Category;

import ch.almeida.seam3.weld.data.MemberListProducer;
import ch.almeida.seam3.weld.data.MemberRepository;
import ch.almeida.seam3.weld.model.Member;

// Adding the @Stateful annotation eliminates need for manual transaction demarcation
// @javax.ejb.Stateful
// The @Model stereotype is a convenience mechanism to make this a request-scoped bean that has an EL name
// Read more about the @Model stereotype in this FAQ: http://seamframework.org/Documentation/WhatIsThePurposeOfTheModelAnnotation
@Model
public class MemberRegistration {
	@Inject
	@Category("seam3-weld")
	private Logger log;

	@Inject
	Messages messages;

	@Inject
	MemberListProducer producer;

	@Inject
	@MemberRepository
	private EntityManager em;

	@Inject
	private UserTransaction utx;

	@Inject
	private Event<Member> memberEventSrc;

	private Member member;

	private transient DataModel<Member> model;

	@Produces
	@Named
	public Member getMember() {
		return member;
	}

	public void save() throws Exception {
		log.info("Saving member: " + member.getName());

		// Delegate:
		this.update();

		messages.info("Member has been successfully saved!");
	}

	public void edit() throws Exception {
		Member selectedMember = model.getRowData();
		if (selectedMember != null) {
			member = selectedMember;
		}
	}

	public void update() throws Exception {
		// UserTransaction only needed when bean is not an EJB
		utx.begin();
		em.joinTransaction();
		if (member.getId() != null) {
			em.refresh(member);
		} else {
			em.persist(member);
		}
		utx.commit();
		memberEventSrc.fire(member);
		initNewMember();
	}

	public void delete() throws Exception {
		Member selectedMember = model.getRowData();
		if (selectedMember != null) {
			log.info("Deleting member #" + selectedMember.getId());

			utx.begin();

			// Lookup selected member:
			selectedMember = em.merge(selectedMember);
			em.remove(selectedMember);
			utx.commit();

			// Notify:
			memberEventSrc.fire(selectedMember);
			messages.info("Member {0} has been successfully deleted!", selectedMember.getId());
		} else {
			messages.error("No member available!");
		}
	}

	@PostConstruct
	public void initNewMember() {
		member = new Member();
	}

	public DataModel<Member> getModel() {
		if (model == null) {
			model = new ListDataModel<Member>(producer.getMembers());
		}
		return model;
	}

	public Date getLastUpdated() {
		return new Date();
	}
}
