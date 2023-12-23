package com.rb.crafty.sheets

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.rb.crafty.R
import com.rb.crafty.adapters.UserElementsAdapter
import com.rb.crafty.dataObjects.ElementData
import com.rb.crafty.databinding.FragmentImportElementSheetBinding
import com.rb.crafty.databinding.FragmentUserElementsSheetBinding
import com.rb.crafty.utils.AnimUtils
import com.rb.crafty.utils.AppUtils
import com.rb.crafty.utils.ColourUtils
import java.io.ObjectInputStream


class UserElementsSheet() : BottomSheetDialogFragment() {
    lateinit var binding: FragmentUserElementsSheetBinding

    lateinit var elementsList: MutableList<ElementData>
    lateinit var adapter: UserElementsAdapter

    lateinit var firestore: FirebaseFirestore
    var user: FirebaseUser? = null

    lateinit var elementLauncher: ActivityResultLauncher<Intent>

    lateinit var colourUtils: ColourUtils

    interface UserElementsSheetListener {
        fun onElementClicked(elementData: ElementData)
    }

    lateinit var listener: UserElementsSheetListener


    companion object {
        val FROM_PROFILE = "profile"
        val FROM_CREATOR = "creator"
    }

    var callFrom = FROM_PROFILE

    constructor(callFrom: String, listener: UserElementsSheetListener) : this() {
        this.callFrom = callFrom
        this.listener = listener
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, R.style.PurpleBottomSheetDialogStyle)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserElementsSheetBinding.inflate(inflater, container, false)

        firestore = Firebase.firestore
        user = Firebase.auth.currentUser

        colourUtils = ColourUtils(requireActivity())

        elementsList = ArrayList()
        adapter = UserElementsAdapter(requireActivity(), elementsList, callFrom, listener)
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.userElementsSheetRecy.layoutManager = layoutManager
        binding.userElementsSheetRecy.adapter = adapter

        if (user != null) {
            firestore.collection(AppUtils.USER_ASSETS_COLLECTION).document("doc")
                .collection(user!!.uid).document("doc")
                .collection(AppUtils.CRAFTY_ELEMENTS_COLLECTION).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    for (doc in it.result.documents) {
                        val data = doc.toObject<ElementData>()!!
                        elementsList.add(data)
                        adapter.notifyItemInserted(elementsList.size - 1)
                    }

                    if (it.result.isEmpty) {
                        binding.userElementsEmptyCard.visibility = View.VISIBLE
                    }
                    else {
                        binding.userElementsEmptyCard.visibility = View.GONE
                    }

                } else {
                    AppUtils.buildSnackbar(
                        requireActivity(),
                        "Unable to load your elements, try again later",
                        binding.root
                    ).show()
                }
            }
        }

        elementLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val contentFileName = AppUtils.getContentFileName(requireActivity(), it.data!!.data!!)
                if (contentFileName.endsWith(".ced", false)) {
                    importElement(it.data!!.data!!)
                } else {
                    AppUtils.buildSnackbar(requireActivity(), "Please select a '.ced' file", binding.root).show()
                }

            }
        }


        binding.userElementsSheetImportButton.setOnClickListener {
            val anim = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.setDataAndType(MediaStore.Files.getContentUri("external"), "application/*")
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.flags =
                        Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    if (AppUtils.hasStoragePermission(requireActivity())) {
                        if (user != null) {
                            elementLauncher.launch(intent)
                        }
                    } else {

                        AppUtils.buildStoragePermission(requireActivity()).show()
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(anim)
        }

        return binding.root
    }


    fun importElement(filePath: Uri) {
        try {
            //Get the data from the file.
            val ois = ObjectInputStream(requireActivity().contentResolver.openInputStream(filePath))
            val elementData = ois.readObject() as ElementData

            val elementImportSheet = ImportElementSheet(elementData, object : ImportElementSheet.ImportElementSheetListener {
                override fun onElementImported(elementData: ElementData) {
                    elementsList.add(elementData)
                    adapter.notifyItemInserted(elementsList.size - 1)
                    AppUtils.buildSnackbar(requireActivity(), "Added the element to your list", binding.root).show()
                }

                override fun onElementImportFailed(text: String) {
                    AppUtils.buildSnackbar(requireActivity(), text, binding.root).show()
                }
            })
            elementImportSheet.show(childFragmentManager, "UseCaseOne")
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }


}