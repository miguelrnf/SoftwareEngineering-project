describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft');
    cy.contains('Logout').click();
  });

  it('login and edit a Tournament', () => {
    cy.createTournament('Demo tournament', '30', true, false);
    cy.createTournament('Demo tournament Edited', '10', true, true);
    cy.assertT('Demo tournament Edited');
    cy.deleteTournament('Demo tournament Edited');
  });
});
