#include<iostream>
#include<GL/graph.h>
#include<cstring>
#include<stdio.h>
using namespace std;
char c='x';
void myReshape(int w, int h);
void printw(float x, float y, const char *st)
{
	int len,i;
	GLvoid *font_styles;
	font_styles = GLUT_BITMAP_HELVETICA_18;
	char cmdbuf[500];
	glRasterPos2f(x,y);//Initial position to start with.
	snprintf(cmdbuf, sizeof(cmdbuf), "%s",st);
	len=strlen(cmdbuf);
	for(i=0;i<len;i++)
	{
		glutBitmapCharacter(font_styles,cmdbuf[i]); //printing each character.
	}
}
void displayCone()
{
	int w=glutGet(GLUT_WINDOW_WIDTH);
	int h=glutGet(GLUT_WINDOW_HEIGHT);
	myReshape(w,h);
	switch(c)
	{
		case 'h':
			glMatrixMode(GL_MODELVIEW);
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			glLoadIdentity();
			gluPerspective(90,1,-1,1);
			gluLookAt(2,0,0,0,0,0,0,1,0);
			break;
		case 'c':
			glMatrixMode(GL_MODELVIEW);
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			glLoadIdentity();
			gluPerspective(90,4/5,-4,5);
			gluLookAt(0,8,0,0,0,0,0,0,1);
			break;
		case 'p':
			glMatrixMode(GL_MODELVIEW);
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			glLoadIdentity();
			gluPerspective(45,1,-8,8);
			gluLookAt(0,4,0,5,5,0,0,1,0);
			break;
		case 'e':
			glMatrixMode(GL_MODELVIEW);
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			glLoadIdentity();
			gluPerspective(90,4/5,-4,5);
			gluLookAt(-3,6,0,2,3,0,0,1,0);
			break;
		case 'o':
			glMatrixMode(GL_MODELVIEW);
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			glLoadIdentity();
			gluPerspective(90,1,-1,1);
			gluLookAt(0,2,0,0,1,1,0,1,0);
			break;
		default:
			glMatrixMode(GL_PROJECTION);
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			glLoadIdentity();
			glOrtho(-10,10,-10,10,-10,10);
			glColor3f(1,1,1);
			printw(4,-2,"Choose the display mode : ");
			printw(4,-3,"c - circle ");
			printw(4,-4,"h - hyperbola");
			printw(4,-5,"e - ellipse");
			printw(4,-6,"o - orbital");
			printw(4,-7,"p - parabola");
			break;
			
	}
	glMatrixMode(GL_MODELVIEW);
	glColor3f(1,0,1);
	//Bottom cone
	glEnable (GL_BLEND);
	glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glColor4f(1,0,0,1);
	glPushMatrix();
	glTranslatef(0,-8,0);
	glRotatef(-90,1,0,0);
	glutWireCone(3.2,8,50,50);
	glPopMatrix();
	//Top cone
	glColor4f(1,0,0,1);
	glPushMatrix();
	glTranslatef(0,8,0);
	glRotatef(90,1,0,0);
	glutWireCone(3.2,8,50,50);
	glPopMatrix();
	glFlush();
}
void conicmode(unsigned char key, int x, int y)
{
	switch(key)
	{
		case 'h':
			c='h';
			break;
		case 'c':
			if(c=='h')
				c='x';
			glutPostRedisplay();
			c='c';
			break;
		case 'p':
			if(c=='h')
				c='x';
			glutPostRedisplay();
			c='p';
			break;
		case 'e':
			if(c=='h')
				c='x';
			glutPostRedisplay();
			c='e';
			break;
		case 'o':
			if(c=='h')
				c='x';
			glutPostRedisplay();
			c='o';
			break;
		case 'x':
			c='x';
			break;
		case 'q':
			exit(0);
			break;
		case 'm':
			int w=glutGet(GLUT_WINDOW_WIDTH);
			int h=glutGet(GLUT_WINDOW_HEIGHT);
			myReshape(w,h);
			break;
	}
	glutPostRedisplay();
	
}
void myReshape(int w, int h)
{
	glViewport(0,0,w,h);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	GLfloat r = (GLfloat)h/(GLfloat)w;
	if(w<=h)
		glOrtho(-5,5,-5*r,5*r,-10,10);
	else
		glOrtho(-5/r,5/r,-5,5,-10,10);
	glMatrixMode(GL_MODELVIEW);
	glutPostRedisplay();
}
int main (int argc, char **argv)
{
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB);
	glutInitWindowSize(800,600);
	glutCreateWindow("Conic Sections");
	//glutFullScreen();
	int w=glutGet(GLUT_WINDOW_WIDTH);
	int h=glutGet(GLUT_WINDOW_HEIGHT);
	myReshape(w,h);
	glutDisplayFunc(displayCone);
	glutReshapeFunc(myReshape);
	glutKeyboardFunc(conicmode);
	glClearColor(0.0,0.0,0.0,0.0);
	glutMainLoop();
	return 0;
}
