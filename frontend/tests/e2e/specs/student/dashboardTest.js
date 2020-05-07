describe('Dahsboard Test', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('view dashboard', () => {
    cy.demoStudenDashboard();
  });

  it('view dashboard and open a tournament', () => {
    cy.createTournament('Demo tournament', '30', true);
    cy.listAvailableTournaments();
    cy.signInSignOut('Demo tournament');
    cy.demoStudenDashboard();
    cy.contains('Demo tournament').click();
    cy.closeDialog().click();
    cy.listAvailableTournaments();
    cy.signInSignOut('Demo tournament');
    cy.deleteTournament('Demo tournament');
  });
});
