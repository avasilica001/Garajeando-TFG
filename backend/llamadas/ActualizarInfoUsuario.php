<?php
        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='POST'){
                if (isset($_POST['IdUsuario']) and isset($_POST['Nombre']) and isset($_POST['Apellidos']) and isset($_POST['CorreoElectronico']) and isset($_POST['Direccion']) and isset($_POST['NombreFoto']) and isset($_POST['FotoPerfil'])){
                        $bbdd = new DBOperations();

                        if($bbdd->modificarInfoUsuario($_POST['IdUsuario'],$_POST['CorreoElectronico'],$_POST['Nombre'],$_POST['Apellidos'],$_POST['Direccion'],$_POST['FotoPerfil'],$_POST['NombreFoto'])){
                                $respuesta['error'] = false;
                                $respuesta['mensaje'] = "Datos actualizados correctamente.";
                        }
                }else{
                        $respuesta['error'] = true;
                        $respuesta['mensaje'] = "Faltan campos obligatorios.";
                }
        }else{
                $respuesta['error'] = true;
                $respuesta['mensaje'] = "Petición no válida.";
        }

echo json_encode($respuesta);
?>
