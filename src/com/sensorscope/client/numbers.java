// Author: François Ingelrest
// Date:   08/08/2011

package com.sensorscope.client;

// Java libs
import java.util.Random;

// GWT libs
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class numbers implements EntryPoint
{
    // The values that can be used by the generate() method
    // User's values don't have to be in that set
    private static final int VALID_GENERATE_SET[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 25, 50, 75, 100};

    // Widgets
    private HTML    mLblSolution;
    private Label   mLblError;
    private Button  mBtnSolve;
    private Button  mBtnGenerate;
    private TextBox mTxtTarget;
    private TextBox mTxtInput[];

    public void onModuleLoad()
    {
        HorizontalPanel hPanel;

        mLblError    = new Label();
        mBtnSolve    = new Button("Solve");
        mTxtInput    = new TextBox[6];
        mTxtTarget   = new TextBox();
        mBtnGenerate = new Button("Generate");
        mLblSolution = new HTML();

        // UI: Input numbers
        hPanel = new HorizontalPanel();
        hPanel.setSpacing(10);
        hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        for(int i=0; i<6; ++i)
        {
            mTxtInput[i] = new TextBox();
            hPanel.add(mTxtInput[i]);
        }

        mTxtTarget = new TextBox();

        hPanel.add(new Label(" = "));
        hPanel.add(mTxtTarget);

        RootPanel.get("inputNumbersContainer").add(hPanel);

        // UI: Buttons
        hPanel = new HorizontalPanel();
        hPanel.setSpacing(20);
        hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        hPanel.add(mBtnGenerate);
        hPanel.add(mBtnSolve);

        RootPanel.get("buttonsContainer").add(hPanel);

        // UI: Labels
        RootPanel.get("errorLabelContainer").add(mLblError);
        RootPanel.get("solutionLabelContainer").add(mLblSolution);

        // UI: Focus
        mTxtInput[0].setFocus(true);

        // Handlers
        mBtnGenerate.addClickHandler(new ClickHandler(){
            public void onClick(ClickEvent event)
            {
                generate();
            }
        });

        mBtnSolve.addClickHandler(new ClickHandler(){
            public void onClick(ClickEvent event)
            {
                solve();
            }
        });
    }

    private void generate()
    {
        Random random = new Random();

        for(int i=0; i<6; ++i)
            mTxtInput[i].setText(Integer.toString(VALID_GENERATE_SET[random.nextInt(VALID_GENERATE_SET.length)]));

        // The target values lies in [100;999]
        mTxtTarget.setText(Integer.toString(100 + random.nextInt(900)));

        // Reset labels
        mLblError.setText("");
        mLblSolution.setText("");
    }

    private int fieldToInt(TextBox textbox)
    {
        try
        {
            return Integer.parseInt(textbox.getText());
        }
        catch(Exception e)
        {
            return -1;
        }
    }

    private boolean isValidInput(int input[], int target)
    {
        if(target <= 0)
            return false;

        for(int i=0; i<input.length; ++i)
            if(input[i] <= 0)
                return false;

        return true;
    }

    private void solve()
    {
        mLblSolution.setText("");

        // Get input values
        int target  = fieldToInt(mTxtTarget);
        int input[] = new int[6];

        for(int i=0; i<6; ++i)
            input[i] = fieldToInt(mTxtInput[i]);

        // Valid input?
        if(!isValidInput(input, target))
        {
            mLblError.setText("Input values must be stricly positive integers.");
            return;
        }
        else
            mLblError.setText("");

        // Solve the problem
        Solver solver = new Solver(input, target);

        long start = System.currentTimeMillis();
        solver.solve();
        long end = System.currentTimeMillis();

        if(solver.hasExactSolution()) mLblSolution.setHTML("<b>Exact solution found in " + (end - start) + " ms:</b>");
        else                          mLblSolution.setHTML("<b>Best approximation found in " + (end - start) + " ms:</b>");

        mLblSolution.setHTML(mLblSolution.getHTML() + "<br /><br />" + solver.getOps());
    }
}
